package marketMaster.controller.checkout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import marketMaster.bean.checkout.CheckoutBean;
import marketMaster.bean.checkout.CheckoutDetailsBean;
import marketMaster.bean.product.ProductBean;
import marketMaster.service.checkout.CheckoutService;
import com.fasterxml.jackson.databind.ObjectMapper;
import marketMaster.exception.DataAccessException;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private static final Logger logger = Logger.getLogger(CheckoutController.class.getName());

    @Autowired
    private CheckoutService checkoutService;
    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/checkoutMain")
    public String showCheckoutMain() {
        return "checkout/checkout/index";
    }

    @GetMapping("/list")
    public String getAllCheckouts(Model model) {
        try {
            model.addAttribute("checkouts", checkoutService.getAllCheckouts());
            return "checkout/checkout/GetAllCheckouts";
        } catch (DataAccessException e) {
            logger.severe("獲取所有結帳記錄失敗: " + e.getMessage());
            model.addAttribute("error", "獲取結帳記錄失敗，請稍後再試");
            return "error";
        }
    }

    @GetMapping("/details")
    public String getCheckout(@RequestParam String checkoutId, Model model) {
        try {
            model.addAttribute("checkout", checkoutService.getCheckout(checkoutId));
            return "checkout/checkout/GetCheckout";
        } catch (DataAccessException e) {
            logger.severe("獲取結帳記錄失敗: " + e.getMessage());
            model.addAttribute("error", "獲取結帳記錄失敗，請稍後再試");
            return "error";
        }
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        try {
            model.addAttribute("nextId", checkoutService.generateNextCheckoutId());
            model.addAttribute("checkout", new CheckoutBean());
            model.addAttribute("employees", checkoutService.getAllEmployees());
            return "checkout/checkout/InsertCheckout";
        } catch (DataAccessException e) {
            logger.severe("準備新增結帳表單失敗: " + e.getMessage());
            model.addAttribute("error", "準備新增結帳表單失敗，請稍後再試");
            return "error";
        }
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, String>> addCheckoutWithDetails(@RequestBody Map<String, Object> requestData) {
        logger.info("Received checkout data: " + requestData);
        try {
            CheckoutBean checkout = parseCheckoutData(requestData);
            List<CheckoutDetailsBean> details = parseCheckoutDetails(requestData);
            validateCheckoutData(checkout, details);

            boolean success = checkoutService.insertCheckoutWithDetails(checkout, details);

            if (success) {
                logger.info("Checkout record added successfully");
                return ResponseEntity.ok(Map.of("status", "success", "message", "結帳記錄新增成功"));
            } else {
                logger.warning("Failed to add checkout record");
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "結帳記錄新增失敗"));
            }
        } catch (Exception e) {
            logger.severe("Error processing checkout request: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "處理請求時發生錯誤：" + e.getMessage()));
        }
    }

    @GetMapping("/update")
    public String showUpdateForm(@RequestParam String checkoutId, Model model) {
        try {
            model.addAttribute("checkout", checkoutService.getCheckout(checkoutId));
            return "checkout/checkout/GetUpdateCheckout";
        } catch (DataAccessException e) {
            logger.severe("獲取更新結帳表單失敗: " + e.getMessage());
            model.addAttribute("error", "獲取更新結帳表單失敗，請稍後再試");
            return "error";
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateCheckout(@RequestBody Map<String, String> checkoutData) {
        try {
            CheckoutBean checkout = new CheckoutBean();
            checkout.setCheckoutId(checkoutData.get("checkoutId"));
            checkout.setCustomerTel(checkoutData.get("customerTel"));
            checkout.setEmployeeId(checkoutData.get("employeeId"));
            checkout.setCheckoutTotalPrice(Integer.parseInt(checkoutData.get("checkoutTotalPrice")));
            checkout.setCheckoutDate(dateFormat.parse(checkoutData.get("checkoutDate")));
            checkout.setBonusPoints(Integer.parseInt(checkoutData.get("bonusPoints")));
            checkout.setPointsDueDate(dateFormat.parse(checkoutData.get("pointsDueDate")));

            boolean success = checkoutService.updateCheckout(checkout);
            if (success) {
                return ResponseEntity.ok(Map.of("status", "success", "message", "更新成功"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "更新失敗"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "更新時發生錯誤: " + e.getMessage()));
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteCheckout(@RequestParam String checkoutId) {
        try {
            checkoutService.deleteCheckoutAndDetails(checkoutId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "結帳記錄及其相關明細已成功刪除"));
        } catch (DataAccessException e) {
            logger.severe("刪除結帳記錄失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "刪除結帳記錄及其相關明細失敗: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public String searchCheckoutsByTel(@RequestParam String customerTel, Model model) {
        try {
            model.addAttribute("checkouts", checkoutService.searchCheckoutsByTel(customerTel));
            return "checkout/checkout/SearchResults";
        } catch (DataAccessException e) {
            logger.severe("搜索結帳記錄失敗: " + e.getMessage());
            model.addAttribute("error", "搜索結帳記錄失敗，請稍後再試");
            return "error";
        }
    }

    @GetMapping("/dailySalesReport")
    public String getDailySalesReport(Model model) {
        try {
            model.addAttribute("dailySalesReport", checkoutService.getDailySalesReport());
            return "checkout/checkout/DailySalesReport";
        } catch (DataAccessException e) {
            logger.severe("獲取每日銷售報告失敗: " + e.getMessage());
            model.addAttribute("error", "獲取每日銷售報告失敗，請稍後再試");
            return "error";
        }
    }

    @GetMapping("/summary")
    public String getCheckoutSummary(Model model) {
        try {
            model.addAttribute("checkoutSummary", checkoutService.getCheckoutSummary());
            return "checkout/checkout/CheckoutSummary";
        } catch (DataAccessException e) {
            logger.severe("獲取結帳總表失敗: " + e.getMessage());
            model.addAttribute("error", "獲取結帳總表失敗，請稍後再試");
            return "error";
        }
    }

    @GetMapping("/nextId")
    @ResponseBody
    public ResponseEntity<String> getNextCheckoutId() {
        try {
            return ResponseEntity.ok(checkoutService.generateNextCheckoutId());
        } catch (DataAccessException e) {
            logger.severe("生成下一個結帳ID失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("生成結帳ID失敗");
        }
    }

    @GetMapping("/employees")
    public String getAllEmployees(Model model) {
        try {
            model.addAttribute("employees", checkoutService.getAllEmployees());
            return "employee/EmployeeList";
        } catch (DataAccessException e) {
            logger.severe("獲取所有員工資料失敗: " + e.getMessage());
            model.addAttribute("error", "獲取員工資料失敗，請稍後再試");
            return "error";
        }
    }

    @GetMapping("/products")
    @ResponseBody
    public ResponseEntity<List<ProductBean>> getProductNames(@RequestParam String category) {
        try {
            return ResponseEntity.ok(checkoutService.getProductNamesByCategory(category));
        } catch (DataAccessException e) {
            logger.severe("獲取產品名稱失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }

    @PostMapping("/updateTotalAndBonus")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateTotalAndBonus(
            @RequestParam String checkoutId,
            @RequestParam BigDecimal totalAmount,
            @RequestParam int bonusPoints) {
        try {
            checkoutService.updateTotalAndBonus(checkoutId, totalAmount, bonusPoints);
            return ResponseEntity.ok(Map.of("status", "success", "message", "總金額和紅利點數已更新"));
        } catch (DataAccessException e) {
            logger.severe("更新總金額和紅利點數失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "更新失敗: " + e.getMessage()));
        }
    }
    
    // 輔助方法

    private CheckoutBean parseCheckoutData(Map<String, Object> requestData) throws InvalidCheckoutDataException {
        CheckoutBean checkout = new CheckoutBean();
        try {
            checkout.setCheckoutId((String) requestData.get("checkoutId"));
            checkout.setCustomerTel((String) requestData.get("customerTel"));
            checkout.setEmployeeId((String) requestData.get("employeeId"));
            checkout.setCheckoutDate(new Date()); // 設置為當前日期，或從請求中獲取
            // 設置其他必要的屬性...
        } catch (Exception e) {
            throw new InvalidCheckoutDataException("解析結帳基本資訊失敗: " + e.getMessage());
        }
        return checkout;
    }

    private List<CheckoutDetailsBean> parseCheckoutDetails(Map<String, Object> requestData) throws InvalidCheckoutDataException {
        try {
            String detailsJson = objectMapper.writeValueAsString(requestData.get("details"));
            return checkoutService.parseCheckoutDetails(detailsJson);
        } catch (Exception e) {
            throw new InvalidCheckoutDataException("解析結帳明細失敗: " + e.getMessage());
        }
    }

    private void validateCheckoutData(CheckoutBean checkout, List<CheckoutDetailsBean> details) throws InvalidCheckoutDataException {
        if (checkout.getCheckoutId() == null || checkout.getCheckoutId().isEmpty()) {
            throw new InvalidCheckoutDataException("結帳 ID 不能為空");
        }
        if (checkout.getCustomerTel() == null || checkout.getCustomerTel().isEmpty()) {
            throw new InvalidCheckoutDataException("顧客電話不能為空");
        }
        if (checkout.getEmployeeId() == null || checkout.getEmployeeId().isEmpty()) {
            throw new InvalidCheckoutDataException("員工 ID 不能為空");
        }
        if (details.isEmpty()) {
            throw new InvalidCheckoutDataException("結帳明細不能為空");
        }
        // 可以添加更多的驗證邏輯...
    }

    // 自定義異常類
    private static class InvalidCheckoutDataException extends Exception {
        public InvalidCheckoutDataException(String message) {
            super(message);
        }
    }

}