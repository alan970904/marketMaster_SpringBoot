//package marketMaster.controller.checkout.front;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import marketMaster.DTO.checkout.CartDTO;
//import marketMaster.service.checkout.CheckoutService;
//import marketMaster.service.product.ProductService;
//import marketMaster.bean.checkout.CheckoutDetailsBean;
//import marketMaster.bean.product.ProductBean;
//import marketMaster.exception.DataAccessException;
//import java.util.List;
//import java.util.Map;
//import java.util.logging.Logger;
//
//@Controller
//@RequestMapping("/cart")
//public class CartController {
//    
//    private static final Logger logger = Logger.getLogger(CartController.class.getName());
//    
//    @Autowired
//    private CheckoutService checkoutService;
//    
//    @Autowired
//    private ProductService productService;
//
//    // 顯示購物車頁面
//    @GetMapping
//    public String showCart(Model model) {
//        try {
//            // 獲取商品分類列表
//            List<String> categories = productService.getAllCategories();
//            model.addAttribute("categories", categories);
//            return "checkout/cart/testCart";
//        } catch (Exception e) {
//            logger.severe("載入購物車頁面失敗: " + e.getMessage());
//            model.addAttribute("error", "系統錯誤，請稍後再試");
//            return "error";
//        }
//    }
//
//    // 根據類別獲取商品
//    @GetMapping("/products/{category}")
//    @ResponseBody
//    public ResponseEntity<?> getProductsByCategory(@PathVariable String category) {
//        try {
//            List<ProductBean> products = checkoutService.getProductNamesByCategory(category);
//            return ResponseEntity.ok(products);
//        } catch (DataAccessException e) {
//            logger.severe("獲取商品列表失敗: " + e.getMessage());
//            return ResponseEntity.badRequest().body(Map.of("error", "獲取商品列表失敗"));
//        }
//    }
//
//    // 驗證購物車內容
//    @PostMapping("/validate")
//    @ResponseBody
//    public ResponseEntity<?> validateCart(@RequestBody CartDTO cart) {
//        try {
//            Map<String, Object> validationResult = checkoutService.validateCartItems(
//                cart.getItems().stream()
//                    .map(item -> convertToCheckoutDetail(item))
//                    .toList()
//            );
//            return ResponseEntity.ok(validationResult);
//        } catch (Exception e) {
//            logger.severe("驗證購物車失敗: " + e.getMessage());
//            return ResponseEntity.badRequest()
//                .body(Map.of("error", "驗證購物車失敗: " + e.getMessage()));
//        }
//    }
//
//    // 轉換購物車項目為結帳明細
//    private CheckoutDetailsBean convertToCheckoutDetail(CartDTO.CartItemDTO item) {
//        CheckoutDetailsBean detail = new CheckoutDetailsBean();
//        detail.setProductId(item.getProductId());
//        detail.setNumberOfCheckout(item.getQuantity());
//        detail.setProductPrice(item.getPrice());
//        return detail;
//    }
//}