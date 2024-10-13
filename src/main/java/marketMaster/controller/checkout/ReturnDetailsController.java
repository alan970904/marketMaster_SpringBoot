package marketMaster.controller.checkout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import marketMaster.bean.checkout.ReturnDetailsBean;
import marketMaster.service.checkout.ReturnDetailsService;
import marketMaster.exception.DataAccessException;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/returnProduct/returnDetails")
public class ReturnDetailsController {

    @Autowired
    private ReturnDetailsService returnDetailsService;

    @GetMapping("/list")
    public String getAllReturnDetails(Model model) {
        model.addAttribute("returnDetails", returnDetailsService.getAllReturnDetails());
        return "checkout/returnDetails/GetAllReturnDetails";
    }

    @GetMapping("/listByReturnId")
    public String getPartReturnDetails(@RequestParam String returnId, Model model) {
        model.addAttribute("returnDetailsList", returnDetailsService.getPartReturnDetails(returnId));
        return "checkout/returnDetails/GetPartReturnDetails";
    }

    @GetMapping("/details")
    public String getReturnDetails(@RequestParam String returnId, @RequestParam String checkoutId, @RequestParam String productId, Model model) {
        try {
            model.addAttribute("returnDetails", returnDetailsService.getReturnDetails(returnId, checkoutId, productId));
            return "checkout/returnDetails/GetReturnDetails";
        } catch (DataAccessException e) {
            model.addAttribute("error", "獲取退貨明細失敗：" + e.getMessage());
            return "checkout/returnDetails/Error";
        }
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("returnDetails", new ReturnDetailsBean());
        return "checkout/returnDetails/AddReturnDetails";
    }

    @PostMapping("/add")
    public String addReturnDetails(@ModelAttribute ReturnDetailsBean returnDetails, Model model) {
        try {
            returnDetailsService.addReturnDetails(returnDetails);
            model.addAttribute("message", "退貨明細新增成功");
            return "checkout/returnDetails/Result";
        } catch (DataAccessException e) {
            model.addAttribute("error", "新增退貨明細時發生錯誤：" + e.getMessage());
            return "checkout/returnDetails/Error";
        }
    }

    @GetMapping("/update")
    public String showUpdateForm(@RequestParam String returnId, @RequestParam String checkoutId, @RequestParam String productId, Model model) {
        model.addAttribute("returnDetails", returnDetailsService.getReturnDetails(returnId, checkoutId, productId));
        return "checkout/returnDetails/UpdateReturnDetails";
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateReturnDetails(@RequestBody ReturnDetailsBean returnDetails) {
        try {
            returnDetailsService.updateReturnDetails(returnDetails);
            return ResponseEntity.ok(Map.of("status", "success", "message", "更新成功"));
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteReturnDetails(@RequestBody Map<String, String> request) {
        try {
            String returnId = request.get("returnId");
            String checkoutId = request.get("checkoutId");
            String productId = request.get("productId");
            returnDetailsService.deleteReturnDetails(returnId, checkoutId, productId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "退貨明細已成功刪除"));
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "刪除退貨明細失敗: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public String searchByProductId(@RequestParam String productId, Model model) {
        model.addAttribute("returnDetails", returnDetailsService.searchReturnDetailsByProductId(productId));
        return "checkout/returnDetails/SearchResults";
    }

    @GetMapping("/comparisonTable")
    public String getReturnComparisonTable(Model model) {
        model.addAttribute("comparisonTable", returnDetailsService.getReturnComparisonTable());
        return "checkout/returnDetails/ReturnComparisonTable";
    }

    @GetMapping("/returnRates")
    public String getProductReturnRates(Model model) {
        model.addAttribute("returnRates", returnDetailsService.getProductReturnRates());
        return "checkout/returnDetails/ProductReturnRates";
    }

    @PostMapping("/updateStatus")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateReturnStatus(
            @RequestParam String returnId,
            @RequestParam String checkoutId,
            @RequestParam String productId,
            @RequestParam String status) {
        try {
            returnDetailsService.updateReturnStatus(returnId, checkoutId, productId, status);
            return ResponseEntity.ok(Map.of("status", "success", "message", "退貨狀態更新成功"));
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "更新失敗: " + e.getMessage()));
        }
    }
    
    @GetMapping("/byCheckoutAndProduct")
    public String getReturnDetailsByCheckoutAndProduct(@RequestParam String checkoutId, @RequestParam String productId, Model model) {
        try {
            List<ReturnDetailsBean> returnDetails = returnDetailsService.findByCheckoutIdAndProductId(checkoutId, productId);
            model.addAttribute("returnDetails", returnDetails);
            return "checkout/returnDetails/ReturnDetailsByCheckoutAndProduct";
        } catch (DataAccessException e) {
            model.addAttribute("error", "獲取退貨明細失敗：" + e.getMessage());
            return "checkout/returnDetails/Error";
        }
    }

    @GetMapping("/totalReturnsByProduct")
    public String getTotalReturnsByProduct(@RequestParam String productId, Model model) {
        try {
            Integer totalReturns = returnDetailsService.getTotalReturnsByProduct(productId);
            model.addAttribute("productId", productId);
            model.addAttribute("totalReturns", totalReturns);
            return "checkout/returnDetails/TotalReturnsByProduct";
        } catch (DataAccessException e) {
            model.addAttribute("error", "獲取商品總退貨量失敗：" + e.getMessage());
            return "checkout/returnDetails/Error";
        }
    }

    @GetMapping("/returnReasons")
    public String getReturnReasonStatistics(Model model) {
        try {
            List<Map<String, Object>> statistics = returnDetailsService.getReturnReasonStatistics();
            model.addAttribute("statistics", statistics);
            return "checkout/returnDetails/ReturnReasonStatistics";
        } catch (DataAccessException e) {
            model.addAttribute("error", "獲取退貨原因統計失敗：" + e.getMessage());
            return "checkout/returnDetails/Error";
        }
    }
    
}