package marketMaster.controller.checkout.front;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import marketMaster.bean.checkout.CheckoutBean;
import marketMaster.bean.checkout.CheckoutDetailsBean;
import marketMaster.bean.product.InventoryCheckBean;
import marketMaster.bean.product.ProductBean;
import marketMaster.exception.DataAccessException;
import marketMaster.service.checkout.CheckoutService;
import marketMaster.service.product.InventoryCheckService;
import marketMaster.service.product.ProductService;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * 前台購物車控制器
 * 處理購物相關功能，包含商品展示、購物車操作等
 */
@Controller
@RequestMapping("/front/cart")
public class ShoppingCartController {
    
    private static final Logger logger = Logger.getLogger(ShoppingCartController.class.getName());
    
    @Autowired
    private CheckoutService checkoutService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
	private InventoryCheckService inventoryCheckService;
    
    
    @GetMapping
	public String getAllProduct(@RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
			@RequestParam(value = "size", defaultValue = "10") Integer pageSize, Model m) {
		Page<ProductBean> products = productService.findAllProduct(pageNumber, pageSize);
		List<InventoryCheckBean> inventoryCheck = inventoryCheckService.findAllInventoryCheck();
		m.addAttribute("products", products);
		m.addAttribute("inventoryCheck", inventoryCheck);
		return "checkout/checkout/cart/testCart";
	}
    
    /**
     * 顯示購物車主頁
     */
    @GetMapping("/cart")
    public String showShoppingCart(Model model) {
        try {
            // 獲取商品類別列表
            List<String> categories = productService.getAllCategories();
            model.addAttribute("categories", categories);
            
            // 獲取員工列表，用於結帳時選擇
            model.addAttribute("employees", checkoutService.getAllEmployees());
            
            return "checkout/checkout/cart/ShoppingCart";
        } catch (Exception e) {
            logger.severe("載入購物車頁面失敗: " + e.getMessage());
            model.addAttribute("error", "系統錯誤，請稍後再試");
            return "error";
        }
    }
    
    /**
     * 根據類別獲取商品清單
     */
    @GetMapping("/products")
    @ResponseBody
    public ResponseEntity<?> getProductsByCategory(@RequestParam String category) {
        try {
            List<ProductBean> products = checkoutService.getProductNamesByCategory(category);
            
            // 轉換為前端所需的資料格式，包含圖片轉換為Base64
            List<Map<String, Object>> productList = products.stream()
                .map(product -> {
                    Map<String, Object> productMap = new HashMap<>();
                    productMap.put("productId", product.getProductId());
                    productMap.put("productName", product.getProductName());
                    productMap.put("productPrice", product.getProductPrice());
                    productMap.put("numberOfInventory", product.getNumberOfInventory());
                    if (product.getProductPhoto() != null) {
                        String base64Image = Base64.getEncoder().encodeToString(product.getProductPhoto());
                        productMap.put("productImage", "data:image/jpeg;base64," + base64Image);
                    }
                    return productMap;
                })
                .toList();
            
            return ResponseEntity.ok(productList);
        } catch (DataAccessException e) {
            logger.severe("獲取商品清單失敗: " + e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", "獲取商品清單失敗: " + e.getMessage()));
        }
    }
    
    /**
     * 獲取商品詳細資訊
     */
    @GetMapping("/product/{productId}")
    @ResponseBody
    public ResponseEntity<?> getProductDetails(@PathVariable String productId) {
        try {
            ProductBean product = productService.getProduct(productId);
            if (product == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            logger.severe("獲取商品詳細資訊失敗: " + e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", "獲取商品詳細資訊失敗: " + e.getMessage()));
        }
    }
    
    /**
     * 檢查商品庫存
     */
    @GetMapping("/check-stock/{productId}")
    @ResponseBody
    public ResponseEntity<?> checkProductStock(
            @PathVariable String productId,
            @RequestParam int quantity) {
        try {
            ProductBean product = productService.getProduct(productId);
            if (product == null) {
                return ResponseEntity.notFound().build();
            }
            
            boolean hasStock = product.getNumberOfInventory() >= quantity;
            return ResponseEntity.ok(Map.of(
                "hasStock", hasStock,
                "currentStock", product.getNumberOfInventory()
            ));
        } catch (Exception e) {
            logger.severe("檢查商品庫存失敗: " + e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", "檢查商品庫存失敗: " + e.getMessage()));
        }
    }
    
    /**
     * 獲取下一個結帳編號
     */
    @GetMapping("/next-checkout-id")
    @ResponseBody
    public ResponseEntity<String> getNextCheckoutId() {
        try {
            String nextId = checkoutService.generateNextCheckoutId();
            return ResponseEntity.ok(nextId);
        } catch (DataAccessException e) {
            logger.severe("獲取結帳編號失敗: " + e.getMessage());
            return ResponseEntity.badRequest().body("獲取結帳編號失敗");
        }
    }
    
    /**
     * 驗證購物車內容
     */
    @PostMapping("/validate-cart")
    @ResponseBody
    public ResponseEntity<?> validateCart(@RequestBody List<Map<String, Object>> cartItems) {
        try {
            // 轉換購物車項目為CheckoutDetailsBean列表
            List<CheckoutDetailsBean> details = cartItems.stream()
                .map(item -> {
                    CheckoutDetailsBean detail = new CheckoutDetailsBean();
                    detail.setProductId((String) item.get("productId"));
                    detail.setNumberOfCheckout(((Number) item.get("quantity")).intValue());
                    detail.setProductPrice(((Number) item.get("price")).intValue());
                    return detail;
                })
                .collect(Collectors.toList());

            Map<String, Object> validationResult = checkoutService.validateCartItems(details);
            return ResponseEntity.ok(validationResult);
        } catch (Exception e) {
            logger.severe("驗證購物車失敗: " + e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", "驗證購物車失敗: " + e.getMessage()));
        }
    }
    
    /**
     * 顯示結帳頁面
     */
    @GetMapping("/checkout")
    public String showCheckoutPage(Model model) {
        try {
            // 獲取下一個結帳編號
            String nextCheckoutId = checkoutService.generateNextCheckoutId();
            model.addAttribute("nextCheckoutId", nextCheckoutId);
            
            // 獲取員工列表（用於選擇結帳員工）
            model.addAttribute("employees", checkoutService.getAllEmployees());
            
            return "checkout/cart/Checkout";
        } catch (Exception e) {
            logger.severe("載入結帳頁面失敗: " + e.getMessage());
            model.addAttribute("error", "系統錯誤，請稍後再試");
            return "error";
        }
    }
    
    /**
     * 獲取商品類別列表
     */
    @GetMapping("/categories")
    @ResponseBody
    public ResponseEntity<?> getCategories() {
        try {
            List<String> categories = productService.getAllCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            logger.severe("獲取商品類別失敗: " + e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", "獲取商品類別失敗: " + e.getMessage()));
        }
    }
    
    /**
     * 進行結帳處理
     */
    @PostMapping("/process-checkout")
    @ResponseBody
    public ResponseEntity<?> processCheckout(@RequestBody Map<String, Object> checkoutData) {
        try {
            // 解析結帳資料
            String checkoutId = (String) checkoutData.get("checkoutId");
            String customerTel = (String) checkoutData.get("customerTel");
            String employeeId = (String) checkoutData.get("employeeId");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) checkoutData.get("items");

            // 建立結帳實體
            CheckoutBean checkout = new CheckoutBean();
            checkout.setCheckoutId(checkoutId);
            checkout.setCustomerTel(customerTel);
            checkout.setEmployeeId(employeeId);
            checkout.setCheckoutDate(new Date());

            // 建立結帳明細
            List<CheckoutDetailsBean> details = items.stream()
                .map(item -> {
                    CheckoutDetailsBean detail = new CheckoutDetailsBean();
                    detail.setCheckoutId(checkoutId);
                    detail.setProductId((String) item.get("productId"));
                    detail.setNumberOfCheckout(((Number) item.get("quantity")).intValue());
                    detail.setProductPrice(((Number) item.get("price")).intValue());
                    return detail;
                })
                .toList();

            // 進行結帳處理
            boolean success = checkoutService.processCartCheckout(checkout, details);
            
            if (success) {
                return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "結帳成功",
                    "checkoutId", checkoutId
                ));
            } else {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "結帳處理失敗"));
            }
        } catch (Exception e) {
            logger.severe("結帳處理失敗: " + e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", "結帳處理失敗: " + e.getMessage()));
        }
    }

    
}