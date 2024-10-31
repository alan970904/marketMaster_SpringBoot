package marketMaster.controller.checkout.front;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import marketMaster.bean.checkout.CheckoutBean;
import marketMaster.bean.checkout.CheckoutDetailsBean;
import marketMaster.service.checkout.CheckoutDetailsService;
import marketMaster.service.checkout.CheckoutService;
import marketMaster.exception.DataAccessException;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/front/checkout/checkoutDetails")
public class FrontCheckoutDetailsController {

    @Autowired
    private CheckoutDetailsService checkoutDetailsService;
    
    @Autowired
    private CheckoutService checkoutService;

    @GetMapping("/list")
    public String getAllCheckoutDetails(Model model) {
        model.addAttribute("checkoutDetails", checkoutDetailsService.getAllCheckoutDetails());
        return "checkout/checkoutDetails/front/FrontGetAllCheckoutDetails";
    }

    @GetMapping("/listByCheckoutId")
    public String getPartCheckoutDetails(@RequestParam String checkoutId, Model model) {
        model.addAttribute("checkoutDetailsList", checkoutDetailsService.getPartCheckoutDetails(checkoutId));
        return "checkout/checkoutDetails/front/FrontGetPartCheckoutDetails";
    }

    @GetMapping("/details")
    public String getCheckoutDetails(@RequestParam String checkoutId, @RequestParam String productId, Model model) {
        try {
            model.addAttribute("checkoutDetails", checkoutDetailsService.getCheckoutDetails(checkoutId, productId));
            return "checkout/checkoutDetails/front/FrontGetCheckoutDetails";
        } catch (DataAccessException e) {
            model.addAttribute("error", "獲取結帳明細失敗：" + e.getMessage());
            return "checkout/checkoutDetails/front/FrontError";
        }
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("checkoutDetails", new CheckoutDetailsBean());
        return "checkout/checkoutDetails/front/FrontAddCheckoutDetails";
    }

    @PostMapping("/add")
    public String addCheckoutDetails(@ModelAttribute CheckoutDetailsBean checkoutDetails, Model model) {
        try {
            checkoutDetailsService.addCheckoutDetails(checkoutDetails);
            model.addAttribute("message", "結帳明細新增成功");
            return "checkout/checkoutDetails/front/FrontResult";
        } catch (DataAccessException e) {
            model.addAttribute("error", "新增結帳明細時發生錯誤：" + e.getMessage());
            return "checkout/checkoutDetails/front/FrontError";
        }
    }

    @GetMapping("/update")
    public String showUpdateForm(@RequestParam String checkoutId, @RequestParam String productId, Model model) {
        model.addAttribute("checkoutDetails", checkoutDetailsService.getCheckoutDetails(checkoutId, productId));
        return "checkout/checkoutDetails/front/FrontUpdateCheckoutDetails";
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateCheckoutDetails(@RequestBody CheckoutDetailsBean checkoutDetails) {
        try {
            // 獲取原始結帳資料及明細
            CheckoutBean checkout = checkoutService.getCheckout(checkoutDetails.getCheckoutId());
            CheckoutDetailsBean originalDetail = checkoutDetailsService.getCheckoutDetails(
                checkoutDetails.getCheckoutId(), 
                checkoutDetails.getProductId()
            );
            int oldTotalPrice = checkout.getCheckoutTotalPrice();
            
            // 更新結帳明細
            checkoutDetailsService.updateCheckoutDetails(checkoutDetails);
            
            // 更新總價及紅利點數
            checkoutService.updateTotalPriceAndBonusPoints(
                checkout.getCheckoutId(), 
                oldTotalPrice,
                checkout.getCustomerTel()
            );
            
            return ResponseEntity.ok(Map.of("status", "success", "message", "更新成功"));
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteCheckoutDetails(@RequestBody Map<String, String> request) {
        try {
            String checkoutId = request.get("checkoutId");
            String productId = request.get("productId");
            
            // 參數驗證
            if (checkoutId == null || checkoutId.trim().isEmpty() || 
                productId == null || productId.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("status", "error", "message", "結帳編號和商品編號不能為空"));
            }
            
            // 先檢查結帳記錄是否存在
            CheckoutBean checkout = checkoutService.getCheckout(checkoutId);
            if (checkout == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", "error", "message", "找不到對應的結帳記錄"));
            }
            
            int oldTotalPrice = checkout.getCheckoutTotalPrice();
            String customerTel = checkout.getCustomerTel();

            try {
                // 刪除結帳明細
                checkoutDetailsService.deleteCheckoutDetails(checkoutId, productId);
                
                // 更新總價和紅利點數（使用新的方法）
                checkoutService.updateTotalPriceAndBonusPoints(
                    checkoutId, 
                    oldTotalPrice,
                    customerTel
                );
                
                return ResponseEntity.ok()
                        .body(Map.of("status", "success", "message", "結帳明細已成功刪除"));
                        
            } catch (DataAccessException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("status", "error", "message", "刪除結帳明細失敗: " + e.getMessage()));
            }
            
        } catch (Exception e) {
            e.printStackTrace(); // 記錄詳細錯誤
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "系統錯誤: " + e.getMessage()));
        }
    }
    @GetMapping("/search")
    public String searchByProductId(@RequestParam String productId, Model model) {
        model.addAttribute("checkoutDetails", checkoutDetailsService.searchCheckoutDetailsByProductId(productId));
        return "checkout/checkoutDetails/front/FrontSearchResults";
    }

    @GetMapping("/returnRates")
    public String getProductReturnRates(Model model) {
        model.addAttribute("returnRates", checkoutDetailsService.getProductReturnRates());
        return "checkout/checkoutDetails/front/FrontProductReturnRates";
    }
    
 // 新增方法以支持新的功能
    @PostMapping("/updateAfterReturn")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateAfterReturn(
            @RequestParam String checkoutId,
            @RequestParam String productId,
            @RequestParam int returnQuantity,
            @RequestParam int returnPrice) {
        try {
            checkoutDetailsService.updateAfterReturn(checkoutId, productId, returnQuantity, returnPrice);
            // 更新總價
            checkoutService.updateTotalPrice(checkoutId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "退貨後更新成功"));
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "更新失敗: " + e.getMessage()));
        }
    }

    @PostMapping("/cancelReturn")
    @ResponseBody
    public ResponseEntity<Map<String, String>> cancelReturn(
            @RequestParam String checkoutId,
            @RequestParam String productId,
            @RequestParam int returnQuantity,
            @RequestParam int returnPrice) {
        try {
            checkoutDetailsService.cancelReturn(checkoutId, productId, returnQuantity, returnPrice);
            // 更新總價
            checkoutService.updateTotalPrice(checkoutId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "取消退貨成功"));
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "取消失敗: " + e.getMessage()));
        }
    }
    
    @GetMapping("/totalSales")
    @ResponseBody
    public ResponseEntity<Integer> getTotalSalesByProduct(@RequestParam String productId) {
        try {
            Integer totalSales = checkoutDetailsService.getTotalSalesByProduct(productId);
            return ResponseEntity.ok(totalSales);
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/topSellingProducts")
    public String getTopSellingProducts(@RequestParam(defaultValue = "10") int limit, Model model) {
        try {
            List<Map<String, Object>> topProducts = checkoutDetailsService.getTopSellingProducts(limit);
            model.addAttribute("topProducts", topProducts);
            return "checkout/checkoutDetails/front/FrontTopSellingProducts";
        } catch (DataAccessException e) {
            model.addAttribute("error", "獲取熱銷商品失敗: " + e.getMessage());
            return "error";
        }
    }

    // 新增方法：根據結帳ID和商品ID獲取結帳明細
    @GetMapping("/getByCheckoutAndProduct")
    @ResponseBody
    public ResponseEntity<CheckoutDetailsBean> getCheckoutDetailsByCheckoutAndProduct(
            @RequestParam String checkoutId,
            @RequestParam String productId) {
        try {
            CheckoutDetailsBean detail = checkoutDetailsService.findByCheckoutIdAndProductId(checkoutId, productId);
            if (detail != null) {
                return ResponseEntity.ok(detail);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @PostMapping("/updateTotalAmount")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateTotalAmount(@RequestParam String checkoutId) {
        try {
            CheckoutBean checkout = checkoutService.getCheckout(checkoutId);
            int totalAmount = checkoutDetailsService.calculateCheckoutTotal(checkoutId);
            int bonusPoints = checkoutService.calculateBonusPoints(totalAmount);
            
            return ResponseEntity.ok(Map.of(
                "totalAmount", totalAmount,
                "bonusPoints", bonusPoints
            ));
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
}