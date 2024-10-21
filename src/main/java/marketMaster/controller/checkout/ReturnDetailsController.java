package marketMaster.controller.checkout;

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

import marketMaster.bean.checkout.ReturnDetailsBean;
import marketMaster.service.checkout.ReturnDetailsService;
import marketMaster.exception.DataAccessException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@Controller
@RequestMapping("/returnProduct/returnDetails")
public class ReturnDetailsController {

    private static final Logger logger = Logger.getLogger(ReturnDetailsController.class.getName());

    @Autowired
    private ReturnDetailsService returnDetailsService;
    
    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/list")
    public String getAllReturnDetails(Model model) {
        try {
            model.addAttribute("returnDetails", returnDetailsService.getAllReturnDetails());
            return "checkout/returnDetails/GetAllReturnDetails";
        } catch (DataAccessException e) {
            logger.severe("獲取所有退貨明細失敗: " + e.getMessage());
            model.addAttribute("error", "獲取退貨明細失敗，請稍後再試");
            return "error";
        }
    }

    @GetMapping("/listByReturnId")
    public String getPartReturnDetails(@RequestParam String returnId, Model model) {
        try {
            model.addAttribute("returnDetailsList", returnDetailsService.getPartReturnDetails(returnId));
            return "checkout/returnDetails/GetPartReturnDetails";
        } catch (DataAccessException e) {
            logger.severe("獲取特定退貨ID的明細失敗: " + e.getMessage());
            model.addAttribute("error", "獲取退貨明細失敗，請稍後再試");
            return "error";
        }
    }

    @GetMapping("/details")
    public String getReturnDetails(@RequestParam String returnId, @RequestParam String originalCheckoutId, @RequestParam String productId, Model model) {
        try {
            model.addAttribute("returnDetails", returnDetailsService.getReturnDetails(returnId, originalCheckoutId, productId));
            return "checkout/returnDetails/GetReturnDetails";
        } catch (DataAccessException e) {
            logger.severe("獲取退貨明細失敗: " + e.getMessage());
            model.addAttribute("error", "獲取退貨明細失敗，請稍後再試");
            return "error";
        }
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("returnDetails", new ReturnDetailsBean());
        return "checkout/returnDetails/AddReturnDetails";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, String>> addReturnDetails(@RequestBody ReturnDetailsBean returnDetails) {
        try {
            returnDetailsService.saveReturnDetails(returnDetails);
            return ResponseEntity.ok(Map.of("status", "success", "message", "退貨明細新增成功"));
        } catch (DataAccessException e) {
            logger.severe("新增退貨明細失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "新增退貨明細失敗: " + e.getMessage()));
        }
    }

    @GetMapping("/update")
    public String showUpdateForm(@RequestParam String returnId, @RequestParam String originalCheckoutId, 
            @RequestParam String productId, Model model) {
        try {
            ReturnDetailsBean returnDetail = returnDetailsService.getReturnDetails(returnId, originalCheckoutId, productId);
            model.addAttribute("returnDetail", returnDetail);
            // 增加額外的商品和發票資訊
            model.addAttribute("product", returnDetail.getCheckoutDetail().getProduct());
            model.addAttribute("invoice", returnDetail.getReturnProduct().getOriginalInvoiceNumber());
            return "checkout/returnDetails/GetUpdateReturnDetails";
        } catch (DataAccessException e) {
            model.addAttribute("error", "獲取更新退貨明細表單失敗");
            return "error";
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateReturnDetails(
            @RequestPart("returnDetail") String returnDetailJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ReturnDetailsBean returnDetail = mapper.readValue(returnDetailJson, ReturnDetailsBean.class);
            
            // 處理照片上傳
            if (file != null && !file.isEmpty()) {
                String fileName = saveFile(file);
                returnDetail.setReturnPhoto(fileName);
            }
            
            // 重新計算小計
            returnDetail.setReturnPrice(returnDetail.getNumberOfReturn() * returnDetail.getProductPrice());
            
            returnDetailsService.saveReturnDetails(returnDetail);
            
            // 更新退貨總金額
            returnDetailsService.updateReturnTotal(returnDetail.getReturnId());
            
            return ResponseEntity.ok(Map.of("status", "success", "message", "更新成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
    
    // 新增檔案儲存方法
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

    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteReturnDetails(@RequestBody Map<String, String> request) {
        try {
            String returnId = request.get("returnId");
            String originalCheckoutId = request.get("originalCheckoutId");
            String productId = request.get("productId");
            returnDetailsService.deleteReturnDetails(returnId, originalCheckoutId, productId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "退貨明細已成功刪除"));
        } catch (DataAccessException e) {
            logger.severe("刪除退貨明細失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "刪除退貨明細失敗: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public String searchByProductId(@RequestParam String productId, Model model) {
        try {
            model.addAttribute("returnDetails", returnDetailsService.searchReturnDetailsByProductId(productId));
            return "checkout/returnDetails/SearchResults";
        } catch (DataAccessException e) {
            logger.severe("搜索退貨明細失敗: " + e.getMessage());
            model.addAttribute("error", "搜索退貨明細失敗，請稍後再試");
            return "error";
        }
    }

    @GetMapping("/comparisonTable")
    public String getReturnComparisonTable(Model model) {
        try {
            model.addAttribute("comparisonTable", returnDetailsService.getReturnComparisonTable());
            return "checkout/returnDetails/ReturnComparisonTable";
        } catch (DataAccessException e) {
            logger.severe("獲取退貨比較表失敗: " + e.getMessage());
            model.addAttribute("error", "獲取退貨比較表失敗，請稍後再試");
            return "error";
        }
    }

    @GetMapping("/returnRates")
    public String getProductReturnRates(Model model) {
        try {
            model.addAttribute("returnRates", returnDetailsService.getProductReturnRates());
            return "checkout/returnDetails/ProductReturnRates";
        } catch (DataAccessException e) {
            logger.severe("獲取產品退貨率失敗: " + e.getMessage());
            model.addAttribute("error", "獲取產品退貨率失敗，請稍後再試");
            return "error";
        }
    }

    @PostMapping("/updateStatus")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateReturnStatus(
            @RequestParam String returnId,
            @RequestParam String originalCheckoutId,
            @RequestParam String productId,
            @RequestParam String status) {
        try {
            returnDetailsService.updateReturnStatus(returnId, originalCheckoutId, productId, status);
            return ResponseEntity.ok(Map.of("status", "success", "message", "退貨狀態更新成功"));
        } catch (DataAccessException e) {
            logger.severe("更新退貨狀態失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "更新失敗: " + e.getMessage()));
        }
    }
    
    @GetMapping("/byOriginalCheckout")
    public String getReturnDetailsByOriginalCheckout(@RequestParam String originalCheckoutId, Model model) {
        try {
            List<ReturnDetailsBean> returnDetails = returnDetailsService.findByOriginalCheckoutId(originalCheckoutId);
            model.addAttribute("returnDetails", returnDetails);
            return "checkout/returnDetails/ReturnDetailsByOriginalCheckout";
        } catch (DataAccessException e) {
            logger.severe("獲取原始結帳ID的退貨明細失敗: " + e.getMessage());
            model.addAttribute("error", "獲取退貨明細失敗，請稍後再試");
            return "error";
        }
    }

    @GetMapping("/byDateRange")
    public String getReturnDetailsByDateRange(
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate,
            Model model) {
        try {
            List<ReturnDetailsBean> returnDetails = returnDetailsService.findReturnDetailsByDateRange(startDate, endDate);
            model.addAttribute("returnDetails", returnDetails);
            return "checkout/returnDetails/ReturnDetailsByDateRange";
        } catch (DataAccessException e) {
            logger.severe("獲取指定日期範圍的退貨明細失敗: " + e.getMessage());
            model.addAttribute("error", "獲取退貨明細失敗，請稍後再試");
            return "error";
        }
    }

    @GetMapping("/productReturnQuantity")
    @ResponseBody
    public ResponseEntity<Integer> getProductReturnQuantityInDateRange(
            @RequestParam String productId,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate) {
        try {
            Integer quantity = returnDetailsService.getProductReturnQuantityInDateRange(productId, startDate, endDate);
            return ResponseEntity.ok(quantity);
        } catch (DataAccessException e) {
            logger.severe("獲取產品在指定日期範圍內的退貨數量失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/totalReturnsByProduct")
    @ResponseBody
    public ResponseEntity<Integer> getTotalReturnsByProduct(@RequestParam String productId) {
        try {
            Integer totalReturns = returnDetailsService.getTotalReturnsByProduct(productId);
            return ResponseEntity.ok(totalReturns);
        } catch (DataAccessException e) {
            logger.severe("獲取商品總退貨量失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/returnReasons")
    public String getReturnReasonStatistics(Model model) {
        try {
            List<Map<String, Object>> statistics = returnDetailsService.getReturnReasonStatistics();
            model.addAttribute("statistics", statistics);
            return "checkout/returnDetails/ReturnReasonStatistics";
        } catch (DataAccessException e) {
            logger.severe("獲取退貨原因統計失敗: " + e.getMessage());
            model.addAttribute("error", "獲取退貨原因統計失敗，請稍後再試");
            return "error";
        }
    }
}