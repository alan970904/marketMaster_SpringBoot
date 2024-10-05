package marketMaster.controller.checkout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import marketMaster.bean.checkout.CheckoutDetailsBean;
import marketMaster.service.checkout.CheckoutDetailsService;
import marketMaster.service.checkout.CheckoutService;
import marketMaster.exception.DataAccessException;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/checkout/checkoutDetails")
public class CheckoutDetailsController {

    @Autowired
    private CheckoutDetailsService checkoutDetailsService;
    
    @Autowired
    private CheckoutService checkoutService;

    @GetMapping("/list")
    public String getAllCheckoutDetails(Model model) {
        model.addAttribute("checkoutDetails", checkoutDetailsService.getAllCheckoutDetails());
        return "checkout/checkoutDetails/GetAllCheckoutDetails";
    }

    @GetMapping("/listByCheckoutId")
    public String getPartCheckoutDetails(@RequestParam String checkoutId, Model model) {
        model.addAttribute("checkoutDetailsList", checkoutDetailsService.getPartCheckoutDetails(checkoutId));
        return "checkout/checkoutDetails/GetPartCheckoutDetails";
    }

    @GetMapping("/details")
    public String getCheckoutDetails(@RequestParam String checkoutId, @RequestParam String productId, Model model) {
        try {
            model.addAttribute("checkoutDetails", checkoutDetailsService.getCheckoutDetails(checkoutId, productId));
            return "checkout/checkoutDetails/GetCheckoutDetails";
        } catch (DataAccessException e) {
            model.addAttribute("error", "獲取結帳明細失敗：" + e.getMessage());
            return "checkout/checkoutDetails/Error";
        }
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("checkoutDetails", new CheckoutDetailsBean());
        return "checkout/checkoutDetails/AddCheckoutDetails";
    }

    @PostMapping("/add")
    public String addCheckoutDetails(@ModelAttribute CheckoutDetailsBean checkoutDetails, Model model) {
        try {
            checkoutDetailsService.addCheckoutDetails(checkoutDetails);
            model.addAttribute("message", "結帳明細新增成功");
            return "checkout/checkoutDetails/Result";
        } catch (DataAccessException e) {
            model.addAttribute("error", "新增結帳明細時發生錯誤：" + e.getMessage());
            return "checkout/checkoutDetails/Error";
        }
    }

    @GetMapping("/update")
    public String showUpdateForm(@RequestParam String checkoutId, @RequestParam String productId, Model model) {
        model.addAttribute("checkoutDetails", checkoutDetailsService.getCheckoutDetails(checkoutId, productId));
        return "checkout/checkoutDetails/UpdateCheckoutDetails";
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateCheckoutDetails(@RequestBody CheckoutDetailsBean checkoutDetails) {
        try {
            checkoutDetailsService.updateCheckoutDetails(checkoutDetails);
            checkoutService.updateTotalPrice(checkoutDetails.getCheckoutId());
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
            checkoutDetailsService.deleteCheckoutDetails(checkoutId, productId);
            checkoutService.updateTotalPrice(checkoutId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "結帳明細已成功刪除"));
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "刪除結帳明細失敗: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public String searchByProductId(@RequestParam String productId, Model model) {
        model.addAttribute("checkoutDetails", checkoutDetailsService.searchCheckoutDetailsByProductId(productId));
        return "checkout/checkoutDetails/SearchResults";
    }

    @GetMapping("/returnRates")
    public String getProductReturnRates(Model model) {
        model.addAttribute("returnRates", checkoutDetailsService.getProductReturnRates());
        return "checkout/checkoutDetails/ProductReturnRates";
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
            return ResponseEntity.ok(Map.of("status", "success", "message", "取消退貨成功"));
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "取消失敗: " + e.getMessage()));
        }
    }
}