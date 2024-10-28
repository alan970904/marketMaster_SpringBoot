package marketMaster.controller.checkout.front;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import marketMaster.DTO.checkout.CheckoutDTO;
import marketMaster.DTO.checkout.ReturnDetailDTO;
import marketMaster.DTO.checkout.ReturnProductDTO;
import marketMaster.bean.checkout.CheckoutBean;
import marketMaster.bean.checkout.ReturnProductBean;
import marketMaster.bean.employee.EmpBean;
import marketMaster.bean.product.ProductBean;
import marketMaster.service.checkout.CheckoutService;
import marketMaster.service.checkout.ReturnProductService;
import marketMaster.exception.DataAccessException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@Controller
@RequestMapping("/front/returnProduct")
public class FrontReturnProductController {

    private static final Logger logger = Logger.getLogger(FrontReturnProductController.class.getName());

    @Autowired
    private ReturnProductService returnProductService;

    @Autowired
    private CheckoutService checkoutService;
    
    @Value("${upload.path}")
    private String uploadPath;
    
    @GetMapping("/returnMain")
    public String showReturnMain() {
        return "checkout/returnProduct/front/FrontIndex";
    }
    
    @GetMapping("/list")
    public String getAllReturnProducts(Model model) {
        try {
            model.addAttribute("returnProducts", returnProductService.getAllReturnProducts());
            return "checkout/returnProduct/front/FrontGetAllReturnProducts";
        } catch (DataAccessException e) {
            logger.severe("獲取所有退貨記錄失敗: " + e.getMessage());
            model.addAttribute("error", "獲取退貨記錄失敗，請稍後再試");
            return "error";
        }
    }

    @GetMapping("/details")
    public String getReturnProduct(@RequestParam String returnId, Model model) {
        try {
            model.addAttribute("returnProduct", returnProductService.getReturnProduct(returnId));
            return "checkout/returnProduct/front/FrontGetReturnProduct";
        } catch (DataAccessException e) {
            logger.severe("獲取退貨記錄失敗: " + e.getMessage());
            model.addAttribute("error", "獲取退貨記錄失敗，請稍後再試");
            return "error";
        }
    }

    @GetMapping("/add")
    public String showAddForm(@RequestParam(required = false) String invoiceNumber, Model model) {
        try {
            model.addAttribute("returnProduct", new ReturnProductBean());
            model.addAttribute("nextId", returnProductService.generateNextReturnId());
            model.addAttribute("invoiceNumber", invoiceNumber);
            model.addAttribute("employees", returnProductService.getActiveEmployees());
            return "checkout/returnProduct/front/FrontInsertReturnProduct";
        } catch (DataAccessException e) {
            model.addAttribute("error", "準備新增退貨表單失敗，請稍後再試");
            return "error";
        }
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, String>> addReturnProductWithDetails(
            @RequestPart("returnData") String returnDataJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ReturnProductDTO returnData = mapper.readValue(returnDataJson, ReturnProductDTO.class);
            
            // 處理文件上傳
            if (files != null && !files.isEmpty()) {
                for (int i = 0; i < files.size(); i++) {
                    MultipartFile file = files.get(i);
                    if (!file.isEmpty()) {
                        String fileName = saveFile(file);
                        if (i < returnData.getReturnProducts().size()) {
                            returnData.getReturnProducts().get(i).setReturnPhoto(fileName);
                        }
                    }
                }
            }
            
            // 新增退貨記錄
            String returnId = returnProductService.addReturnProductWithDetails(returnData);
            
            // 更新原始結帳單狀態
            CheckoutBean checkout = checkoutService.findByInvoiceNumber(returnData.getOriginalInvoiceNumber());
            if (checkout != null) {
                checkout.setCheckoutStatus("已退貨");
                checkout.setRelatedReturnId(returnId);
                checkoutService.updateCheckout(checkout);
            }
            
            return ResponseEntity.ok(Map.of(
                "status", "success", 
                "message", "退貨記錄新增成功",
                "returnId", returnId
            ));
        } catch (Exception e) {
            logger.severe("新增退貨記錄及明細失敗: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "新增退貨記錄及明細失敗: " + e.getMessage()));
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
            ? originalFilename.substring(originalFilename.lastIndexOf("."))
            : "";
        String fileName = UUID.randomUUID().toString() + extension;
        Path filePath = Paths.get(uploadPath, fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }


    @GetMapping("/update")
    public String showUpdateForm(@RequestParam String returnId, Model model) {
        try {
            model.addAttribute("returnProduct", returnProductService.getReturnProduct(returnId));
            model.addAttribute("employees", returnProductService.getActiveEmployees());
            return "checkout/returnProduct/front/FrontUpdateReturnProduct";
        } catch (DataAccessException e) {
            logger.severe("獲取更新退貨表單失敗: " + e.getMessage());
            model.addAttribute("error", "獲取更新退貨表單失敗，請稍後再試");
            return "error";
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateReturnProduct(@RequestBody Map<String, String> request) {
        try {
            ReturnProductBean returnProduct = returnProductService.getReturnProduct(request.get("returnId"));
            returnProduct.setEmployeeId(request.get("employeeId"));
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date returnDate = sdf.parse(request.get("returnDate"));
            returnProduct.setReturnDate(returnDate);
            
            returnProductService.updateReturnProduct(returnProduct);
            return ResponseEntity.ok(Map.of("status", "success", "message", "更新成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteReturnProduct(@RequestBody Map<String, String> request) {
        try {
            String returnId = request.get("returnId");
            returnProductService.deleteReturnProduct(returnId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "退貨記錄已成功刪除"));
        } catch (DataAccessException e) {
            logger.severe("刪除退貨記錄失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "刪除退貨記錄失敗: " + e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public String getReturnSummary(Model model) {
        try {
            model.addAttribute("returnSummary", returnProductService.getReturnSummary());
            return "checkout/returnProduct/front/FrontReturnSummary";
        } catch (DataAccessException e) {
            logger.severe("獲取退貨總表失敗: " + e.getMessage());
            model.addAttribute("error", "獲取退貨總表失敗，請稍後再試");
            return "error";
        }
    }

    @GetMapping("/dailyReport")
    public String getDailyReturnReport(Model model) {
        try {
            model.addAttribute("dailyReturnReport", returnProductService.getDailyReturnReport());
            return "checkout/returnProduct/front/FrontDailyReturnReport";
        } catch (DataAccessException e) {
            logger.severe("獲取每日退貨報告失敗: " + e.getMessage());
            model.addAttribute("error", "獲取每日退貨報告失敗，請稍後再試");
            return "error";
        }
    }

    @GetMapping("/byDateRange")
    public String getReturnsByDateRange(
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate,
            Model model) {
        try {
            model.addAttribute("returnProducts", returnProductService.getReturnsByDateRange(startDate, endDate));
            return "checkout/returnProduct/front/FrontReturnsByDateRange";
        } catch (DataAccessException e) {
            logger.severe("獲取指定日期範圍的退貨記錄失敗: " + e.getMessage());
            model.addAttribute("error", "獲取指定日期範圍的退貨記錄失敗，請稍後再試");
            return "error";
        }
    }

    @GetMapping("/nextId")
    @ResponseBody
    public ResponseEntity<String> getNextReturnId() {
        try {
            return ResponseEntity.ok(returnProductService.generateNextReturnId());
        } catch (DataAccessException e) {
            logger.severe("生成下一個退貨ID失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("生成退貨ID失敗");
        }
    }

    @GetMapping("/byEmployee")
    public String getReturnsByEmployeeId(@RequestParam String employeeId, Model model) {
        try {
            model.addAttribute("returnProducts", returnProductService.getReturnsByEmployeeId(employeeId));
            return "checkout/returnProduct/front/FrontReturnsByEmployee";
        } catch (DataAccessException e) {
            logger.severe("獲取指定員工的退貨記錄失敗: " + e.getMessage());
            model.addAttribute("error", "獲取指定員工的退貨記錄失敗，請稍後再試");
            return "error";
        }
    }

    @PostMapping("/updateTotalPrice")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateTotalPrice(@RequestParam String returnId) {
        try {
            returnProductService.updateTotalPrice(returnId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "退貨總金額更新成功"));
        } catch (DataAccessException e) {
            logger.severe("更新退貨總金額失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "更新退貨總金額失敗: " + e.getMessage()));
        }
    }
    
    @GetMapping("/employees")
    @ResponseBody
    public ResponseEntity<List<EmpBean>> getAllEmployees() {
        try {
            List<EmpBean> employees = returnProductService.getAllEmployees();
            return ResponseEntity.ok(employees);
        } catch (DataAccessException e) {
            logger.severe("獲取所有員工資料失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/products")
    @ResponseBody
    public ResponseEntity<List<ProductBean>> getProductsByCategory(@RequestParam String category) {
        try {
            List<ProductBean> products = returnProductService.getProductNamesByCategory(category);
            return ResponseEntity.ok(products);
        } catch (DataAccessException e) {
            logger.severe("獲取產品名稱失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/dailyTotal")
    @ResponseBody
    public ResponseEntity<Integer> getDailyReturnTotal(@RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date date) {
        try {
            Integer total = returnProductService.getDailyReturnTotal(date);
            return ResponseEntity.ok(total);
        } catch (DataAccessException e) {
            logger.severe("獲取每日退貨總額失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/topReturnRates")
    public String getTopReturnRateProducts(@RequestParam(defaultValue = "10") int n, Model model) {
        try {
            List<Map<String, Object>> topProducts = returnProductService.getTopReturnRateProducts(n);
            model.addAttribute("topProducts", topProducts);
            return "checkout/returnProduct/front/FrontTopReturnRateProducts";
        } catch (DataAccessException e) {
            logger.severe("獲取高退貨率產品失敗: " + e.getMessage());
            model.addAttribute("error", "獲取高退貨率產品失敗，請稍後再試");
            return "error";
        }
    }

    @GetMapping("/byOriginalCheckout")
    public String getReturnsByOriginalCheckoutId(@RequestParam String originalCheckoutId, Model model) {
        try {
            List<ReturnProductBean> returns = returnProductService.getReturnsByOriginalCheckoutId(originalCheckoutId);
            model.addAttribute("returns", returns);
            return "checkout/returnProduct/front/FrontReturnsByOriginalCheckout";
        } catch (DataAccessException e) {
            logger.severe("獲取原始結帳ID的退貨記錄失敗: " + e.getMessage());
            model.addAttribute("error", "獲取退貨記錄失敗，請稍後再試");
            return "error";
        }
    }

    @GetMapping("/byInvoice")
    public String getReturnByOriginalInvoiceNumber(@RequestParam String originalInvoiceNumber, Model model) {
        try {
            ReturnProductBean returnProduct = returnProductService.findByOriginalInvoiceNumber(originalInvoiceNumber);
            if (returnProduct != null) {
                model.addAttribute("returnProduct", returnProduct);
                return "checkout/returnProduct/front/FrontGetReturnProduct";
            } else {
                model.addAttribute("error", "找不到對應的退貨記錄");
                return "error";
            }
        } catch (DataAccessException e) {
            logger.severe("根據發票號碼查詢退貨記錄失敗: " + e.getMessage());
            model.addAttribute("error", "查詢退貨記錄失敗，請稍後再試");
            return "error";
        }
    }
    
    @GetMapping("/invoiceDetails")
    @ResponseBody
    public ResponseEntity<?> getInvoiceDetails(@RequestParam String invoiceNumber) {
        try {
            List<CheckoutDTO> details = returnProductService.getInvoiceDetails(invoiceNumber);
            if (details.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("找不到該發票號碼對應的資料");
            }
            return ResponseEntity.ok(details);
        } catch (DataAccessException e) {
            logger.severe("獲取發票詳細資料失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("獲取發票詳細資料失敗: " + e.getMessage());
        }
    }

    @GetMapping("/totalAmountInDateRange")
    @ResponseBody
    public ResponseEntity<Integer> getTotalReturnAmountInDateRange(
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate) {
        try {
            Integer total = returnProductService.getTotalReturnAmountInDateRange(startDate, endDate);
            return ResponseEntity.ok(total);
        } catch (DataAccessException e) {
            logger.severe("獲取日期範圍內的退貨總額失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/employeeReturnAmount")
    @ResponseBody
    public ResponseEntity<Integer> getEmployeeReturnAmountInDateRange(
            @RequestParam String employeeId,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate) {
        try {
            Integer total = returnProductService.getEmployeeReturnAmountInDateRange(employeeId, startDate, endDate);
            return ResponseEntity.ok(total);
        } catch (DataAccessException e) {
            logger.severe("獲取員工在指定日期範圍內的退貨總額失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/activeEmployees")
    @ResponseBody
    public ResponseEntity<List<EmpBean>> getActiveEmployees() {
        try {
            List<EmpBean> employees = returnProductService.getActiveEmployees();
            return ResponseEntity.ok(employees);
        } catch (DataAccessException e) {
            logger.severe("獲取在職員工資料失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/updateCheckoutStatus")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateCheckoutStatus(
            @RequestParam String invoiceNumber,
            @RequestParam String returnId) {
        try {
            // 更新結帳單狀態
            CheckoutBean checkout = checkoutService.findByInvoiceNumber(invoiceNumber);
            if (checkout != null) {
                checkout.setCheckoutStatus("已退貨");
                checkout.setRelatedReturnId(returnId);
                checkoutService.updateCheckout(checkout);
                return ResponseEntity.ok(Map.of("status", "success", "message", "狀態更新成功"));
            }
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "找不到對應的結帳記錄"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "更新狀態失敗: " + e.getMessage()));
        }
    }
    
    
}