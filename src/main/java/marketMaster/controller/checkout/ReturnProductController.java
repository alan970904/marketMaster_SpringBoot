package marketMaster.controller.checkout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import marketMaster.bean.checkout.ReturnProductBean;
import marketMaster.bean.employee.EmpBean;
import marketMaster.bean.product.ProductBean;
import marketMaster.service.checkout.ReturnProductService;
import marketMaster.exception.DataAccessException;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/returnProduct")
public class ReturnProductController {

    @Autowired
    private ReturnProductService returnProductService;

    @GetMapping("/returnMain")
    public String showCheckoutMain() {
        return "checkout/returnProduct/index";
    }
    
    @GetMapping("/list")
    public String getAllReturnProducts(Model model) {
        model.addAttribute("returnProducts", returnProductService.getAllReturnProducts());
        return "checkout/returnProduct/GetAllReturnProducts";
    }

    @GetMapping("/details")
    public String getReturnProduct(@RequestParam String returnId, Model model) {
        try {
            model.addAttribute("returnProduct", returnProductService.getReturnProduct(returnId));
            return "checkout/returnProduct/GetReturnProduct";
        } catch (DataAccessException e) {
            model.addAttribute("error", "獲取退貨記錄失敗：" + e.getMessage());
            return "checkout/returnProduct/Error";
        }
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("returnProduct", new ReturnProductBean());
        return "checkout/returnProduct/InsertReturnProduct";
    }

    @PostMapping("/add")
    public String addReturnProduct(@ModelAttribute ReturnProductBean returnProduct, Model model) {
        try {
            returnProductService.addReturnProduct(returnProduct);
            model.addAttribute("message", "退貨記錄新增成功");
            return "checkout/returnProduct/Result";
        } catch (DataAccessException e) {
            model.addAttribute("error", "新增退貨記錄時發生錯誤：" + e.getMessage());
            return "checkout/returnProduct/Error";
        }
    }

    @GetMapping("/update")
    public String showUpdateForm(@RequestParam String returnId, Model model) {
        model.addAttribute("returnProduct", returnProductService.getReturnProduct(returnId));
        return "checkout/returnProduct/UpdateReturnProduct";
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateReturnProduct(@RequestBody ReturnProductBean returnProduct) {
        try {
            returnProductService.updateReturnProduct(returnProduct);
            return ResponseEntity.ok(Map.of("status", "success", "message", "更新成功"));
        } catch (DataAccessException e) {
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "刪除退貨記錄失敗: " + e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public String getReturnSummary(Model model) {
        try {
            model.addAttribute("returnSummary", returnProductService.getReturnSummary());
            return "checkout/returnProduct/ReturnSummary";
        } catch (DataAccessException e) {
            model.addAttribute("error", "獲取退貨總表失敗：" + e.getMessage());
            return "checkout/returnProduct/Error";
        }
    }

    @GetMapping("/dailyReport")
    public String getDailyReturnReport(Model model) {
        try {
            model.addAttribute("dailyReturnReport", returnProductService.getDailyReturnReport());
            return "checkout/returnProduct/DailyReturnReport";
        } catch (DataAccessException e) {
            model.addAttribute("error", "獲取每日退貨報告失敗：" + e.getMessage());
            return "checkout/returnProduct/Error";
        }
    }

    @GetMapping("/byDateRange")
    public String getReturnsByDateRange(
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate,
            Model model) {
        try {
            model.addAttribute("returnProducts", returnProductService.getReturnsByDateRange(startDate, endDate));
            return "checkout/returnProduct/ReturnsByDateRange";
        } catch (DataAccessException e) {
            model.addAttribute("error", "獲取指定日期範圍的退貨記錄失敗：" + e.getMessage());
            return "checkout/returnProduct/Error";
        }
    }

    @GetMapping("/nextId")
    @ResponseBody
    public ResponseEntity<String> getNextReturnId() {
        try {
            return ResponseEntity.ok(returnProductService.generateNextReturnId());
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("生成退貨ID失敗");
        }
    }

    @GetMapping("/byEmployee")
    public String getReturnsByEmployeeId(@RequestParam String employeeId, Model model) {
        try {
            model.addAttribute("returnProducts", returnProductService.getReturnsByEmployeeId(employeeId));
            return "checkout/returnProduct/ReturnsByEmployee";
        } catch (DataAccessException e) {
            model.addAttribute("error", "獲取指定員工的退貨記錄失敗：" + e.getMessage());
            return "checkout/returnProduct/Error";
        }
    }

    @PostMapping("/updateTotalPrice")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateTotalPrice(@RequestParam String returnId) {
        try {
            returnProductService.updateTotalPrice(returnId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "退貨總金額更新成功"));
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "更新退貨總金額失敗: " + e.getMessage()));
        }
    }
    
    @GetMapping("/employees")
    public String getAllEmployees(Model model) {
        try {
            List<EmpBean> employees = returnProductService.getAllEmployees();
            model.addAttribute("employees", employees);
            return "checkout/returnProduct/EmployeeList";
        } catch (DataAccessException e) {
            model.addAttribute("error", "獲取員工列表失敗：" + e.getMessage());
            return "checkout/returnProduct/Error";
        }
    }

    @GetMapping("/products")
    public String getProductsByCategory(@RequestParam String category, Model model) {
        try {
            List<ProductBean> products = returnProductService.getProductNamesByCategory(category);
            model.addAttribute("products", products);
            return "checkout/returnProduct/ProductList";
        } catch (DataAccessException e) {
            model.addAttribute("error", "獲取產品列表失敗：" + e.getMessage());
            return "checkout/returnProduct/Error";
        }
    }

    @GetMapping("/dailyTotal")
    public String getDailyReturnTotal(@RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date date, Model model) {
        try {
            Integer total = returnProductService.getDailyReturnTotal(date);
            model.addAttribute("dailyTotal", total);
            model.addAttribute("date", date);
            return "checkout/returnProduct/DailyReturnTotal";
        } catch (DataAccessException e) {
            model.addAttribute("error", "獲取每日退貨總額失敗：" + e.getMessage());
            return "checkout/returnProduct/Error";
        }
    }

    @GetMapping("/topReturnRates")
    public String getTopReturnRateProducts(@RequestParam(defaultValue = "10") int n, Model model) {
        try {
            List<Map<String, Object>> topProducts = returnProductService.getTopReturnRateProducts(n);
            model.addAttribute("topProducts", topProducts);
            return "checkout/returnProduct/TopReturnRateProducts";
        } catch (DataAccessException e) {
            model.addAttribute("error", "獲取高退貨率產品失敗：" + e.getMessage());
            return "checkout/returnProduct/Error";
        }
    }
    
}