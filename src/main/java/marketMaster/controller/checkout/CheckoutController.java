package marketMaster.controller.checkout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import marketMaster.bean.checkout.CheckoutBean;
import marketMaster.bean.checkout.CheckoutDetailsBean;
import marketMaster.bean.employee.EmpBean;
import marketMaster.bean.product.ProductBean;
import marketMaster.service.checkout.CheckoutDetailsService;
import marketMaster.service.checkout.CheckoutService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;
import marketMaster.exception.DataAccessException;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    
    @Autowired
    private CheckoutDetailsService checkoutDetailsService;
    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/checkoutMain")
    public String showCheckoutMain(Model model, HttpSession session) {
        // 從 session 中獲取 employee 對象
        EmpBean employee = (EmpBean) session.getAttribute("employee");
        
        if (employee == null) {
            // 如果 session 中沒有 employee，可以設置一個默認值或者重定向到登錄頁面
            employee = new EmpBean();  // 創建一個空的 EmpBean 對象
            employee.setEmployeeName("未登錄用戶");
        }
        
        // 將 employee 對象添加到模型中
        model.addAttribute("employee", employee);
        
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
            logger.info("Received update request for checkout: " + checkoutData.get("checkoutId"));
            String checkoutId = checkoutData.get("checkoutId");
            CheckoutBean existingCheckout = checkoutService.getCheckout(checkoutId);
            if (existingCheckout == null) {
                logger.warning("Checkout not found: " + checkoutId);
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "找不到指定的結帳記錄"));
            }

            String customerTel = checkoutData.get("customerTel");
            existingCheckout.setCustomerTel(customerTel);
            existingCheckout.setEmployeeId(checkoutData.get("employeeId"));

            String checkoutDateStr = checkoutData.get("checkoutDate");
            if (checkoutDateStr != null && !checkoutDateStr.isEmpty()) {
                existingCheckout.setCheckoutDate(dateFormat.parse(checkoutDateStr));
            }

            // 處理客戶電話為空的情況
            if (customerTel == null || customerTel.trim().isEmpty()) {
                logger.info("Customer tel is empty, setting bonus points to 0 for checkout: " + checkoutId);
                existingCheckout.setBonusPoints(0);
                existingCheckout.setPointsDueDate(null);
            } else {
                logger.info("Customer tel is not empty for checkout: " + checkoutId);
                String bonusPointsStr = checkoutData.get("bonusPoints");
                if (bonusPointsStr != null && !bonusPointsStr.isEmpty()) {
                    existingCheckout.setBonusPoints(Integer.parseInt(bonusPointsStr));
                }
                
                String pointsDueDateStr = checkoutData.get("pointsDueDate");
                if (pointsDueDateStr != null && !pointsDueDateStr.isEmpty()) {
                    existingCheckout.setPointsDueDate(dateFormat.parse(pointsDueDateStr));
                }
            }

            boolean success = checkoutService.updateCheckout(existingCheckout);
            if (success) {
                // 更新結帳明細
                updateCheckoutDetails(checkoutId, checkoutData);
                
                // 獲取更新後的結帳明細
                List<CheckoutDetailsBean> latestDetails = checkoutDetailsService.getPartCheckoutDetails(checkoutId);
                
                logger.info("Checkout updated successfully: " + checkoutId);
                return ResponseEntity.ok(Map.of(
                    "status", "success", 
                    "message", "更新成功",
                    "details", objectMapper.writeValueAsString(latestDetails)
                ));
            } else {
                logger.warning("Failed to update checkout: " + checkoutId);
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "更新失敗"));
            }
        } catch (Exception e) {
            logger.severe("Error updating checkout: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "更新時發生錯誤: " + e.getMessage()));
        }
    }
    
    
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteCheckout(@RequestBody Map<String, String> request) {
        String checkoutId = request.get("checkoutId");
        if (checkoutId == null || checkoutId.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "checkoutId is required"));
        }
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
    @ResponseBody
    public ResponseEntity<List<EmpBean>> getAllEmployees() {
        try {
            logger.info("開始獲取所有員工資料");
            List<EmpBean> employees = checkoutService.getAllEmployees();
            logger.info("成功獲取 " + employees.size() + " 名員工資料");
            return ResponseEntity.ok(employees);
        } catch (DataAccessException e) {
            logger.severe("獲取所有員工資料失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
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
    public ResponseEntity<Map<String, String>> updateTotalAndBonus(@RequestBody Map<String, Object> request) {
        try {
            String checkoutId = (String) request.get("checkoutId");
            BigDecimal totalAmount = new BigDecimal(request.get("totalAmount").toString());
            int bonusPoints = Integer.parseInt(request.get("bonusPoints").toString());
            
            checkoutService.updateTotalAndBonus(checkoutId, totalAmount, bonusPoints);
            return ResponseEntity.ok(Map.of("status", "success", "message", "總金額和紅利點數已更新"));
        } catch (DataAccessException e) {
            logger.severe("更新總金額和紅利點數失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "更新失敗: " + e.getMessage()));
        } catch (Exception e) {
            logger.severe("未預期的錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "未預期的錯誤發生"));
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
            checkout.setPointsDueDate(new Date());
        } catch (Exception e) {
            throw new InvalidCheckoutDataException("解析結帳基本資訊失敗: " + e.getMessage());
        }
        return checkout;
    }

    private List<CheckoutDetailsBean> parseCheckoutDetails(Map<String, Object> requestData) {
        List<Map<String, Object>> detailsData = (List<Map<String, Object>>) requestData.get("details");
        List<CheckoutDetailsBean> details = new ArrayList<>();
        for (Map<String, Object> detailData : detailsData) {
            CheckoutDetailsBean detail = new CheckoutDetailsBean();
            detail.setProductId((String) detailData.get("productId"));
            detail.setNumberOfCheckout((Integer) detailData.get("quantity"));
            detail.setProductPrice((Integer) detailData.get("price"));
            details.add(detail);
        }
        return details;
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
    
    private void updateCheckoutDetails(String checkoutId, Map<String, String> checkoutData) throws Exception {
        String detailsJson = checkoutData.get("checkoutDetails");
        if (detailsJson != null && !detailsJson.isEmpty()) {
            List<Map<String, Object>> detailsList = objectMapper.readValue(detailsJson, new TypeReference<List<Map<String, Object>>>() {});
            
            for (Map<String, Object> detail : detailsList) {
                CheckoutDetailsBean detailBean = new CheckoutDetailsBean();
                detailBean.setCheckoutId(checkoutId);
                detailBean.setProductId((String) detail.get("productId"));
                detailBean.setNumberOfCheckout((Integer) detail.get("quantity"));
                detailBean.setProductPrice((Integer) detail.get("price"));
                
                // 調用 service 方法來更新或插入明細
                checkoutDetailsService.updateOrInsertDetail(detailBean);
            }
        }
    }
    

}