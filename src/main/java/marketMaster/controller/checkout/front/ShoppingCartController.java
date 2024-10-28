package marketMaster.controller.checkout.front;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import marketMaster.DTO.checkout.CartDTO;
import marketMaster.DTO.checkout.CartDTO.CartItemDTO;
import marketMaster.DTO.checkout.CheckoutDTO;
import marketMaster.DTO.checkout.CheckoutDetailDTO;
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
import java.util.stream.Collectors;
import java.util.logging.Logger;

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
        try {
            // 獲取商品資料
            Page<ProductBean> products = productService.findAllProduct(pageNumber, pageSize);
            List<InventoryCheckBean> inventoryCheck = inventoryCheckService.findAllInventoryCheck();
            
            // 轉換為 CartItemDTO
            CartDTO cart = new CartDTO();
            cart.setItems(products.getContent().stream()
                .map(this::convertToCartItem)
                .collect(Collectors.toList()));
            
            m.addAttribute("products", cart.getItems());
            m.addAttribute("inventoryCheck", inventoryCheck);
            m.addAttribute("categories", productService.getAllCategories());
            return "checkout/checkout/front/ShoppingCart";
        } catch (Exception e) {
            logger.severe("載入商品列表失敗: " + e.getMessage());
            m.addAttribute("error", "系統錯誤，請稍後再試");
            return "error";
        }
    }
    
    @GetMapping("/products")
    @ResponseBody
    public ResponseEntity<?> getProductsByCategory(@RequestParam String category) {
        try {
            List<ProductBean> products = checkoutService.getProductNamesByCategory(category);
            CartDTO cart = new CartDTO();
            cart.setItems(products.stream()
                .map(this::convertToCartItem)
                .collect(Collectors.toList()));
            
            return ResponseEntity.ok(cart.getItems());
        } catch (DataAccessException e) {
            logger.severe("獲取商品清單失敗: " + e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", "獲取商品清單失敗: " + e.getMessage()));
        }
    }
    
    @GetMapping("/check-stock/{productId}")
    @ResponseBody
    public ResponseEntity<?> checkProductStock(@PathVariable String productId, @RequestParam int quantity) {
        try {
            ProductBean product = productService.getProduct(productId);
            if (product == null) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("hasStock", product.getNumberOfInventory() >= quantity);
            result.put("currentStock", product.getNumberOfInventory());
            result.put("safeInventory", product.getProductSafeInventory());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.severe("檢查商品庫存失敗: " + e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", "檢查商品庫存失敗: " + e.getMessage()));
        }
    }
    
    @PostMapping("/validate-cart")
    @ResponseBody
    public ResponseEntity<?> validateCart(@RequestBody CartDTO cart) {
        try {
            // 這裡使用現有的 CartDTO 進行驗證
            List<CheckoutDetailsBean> details = convertCartToDetails(cart);
            Map<String, Object> validationResult = checkoutService.validateCartItems(details);
            return ResponseEntity.ok(validationResult);
        } catch (Exception e) {
            logger.severe("驗證購物車失敗: " + e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", "驗證購物車失敗: " + e.getMessage()));
        }
    }
    
    @PostMapping("/process-checkout")
    @ResponseBody
    public ResponseEntity<?> processCheckout(@RequestBody CheckoutDTO checkoutDTO) {
        try {
            // 使用 CheckoutDTO 進行結帳處理
            CheckoutBean checkout = convertToCheckoutBean(checkoutDTO);
            List<CheckoutDetailsBean> details = convertToCheckoutDetails(checkoutDTO.getDetails());
            
            boolean success = checkoutService.processCartCheckout(checkout, details);
            
            if (success) {
                return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "結帳成功",
                    "checkoutId", checkoutDTO.getCheckoutId()
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
    
    // 轉換方法
    private CartItemDTO convertToCartItem(ProductBean product) {
        CartItemDTO item = new CartItemDTO();
        item.setProductId(product.getProductId());
        item.setProductName(product.getProductName());
        item.setPrice(product.getProductPrice());
        item.setQuantity(0); // 初始數量為0
        item.calculateSubtotal();
        return item;
    }
    
    private List<CheckoutDetailsBean> convertCartToDetails(CartDTO cart) {
        return cart.getItems().stream()
            .map(item -> {
                CheckoutDetailsBean detail = new CheckoutDetailsBean();
                detail.setProductId(item.getProductId());
                detail.setNumberOfCheckout(item.getQuantity());
                detail.setProductPrice(item.getPrice());
                detail.setCheckoutPrice(item.getSubtotal());
                return detail;
            })
            .collect(Collectors.toList());
    }
    
    private CheckoutBean convertToCheckoutBean(CheckoutDTO dto) {
        CheckoutBean checkout = new CheckoutBean();
        checkout.setCheckoutId(dto.getCheckoutId());
        checkout.setCustomerTel(dto.getCustomerTel());
        checkout.setEmployeeId(dto.getEmployeeId());
        checkout.setCheckoutDate(dto.getCheckoutDate());
        checkout.setInvoiceNumber(dto.getInvoiceNumber());
        checkout.setCheckoutStatus(dto.getCheckoutStatus());
        checkout.setBonusPoints(dto.getBonusPoints());
        checkout.setCheckoutTotalPrice(dto.getTotalAmount());
        return checkout;
    }
    
    private List<CheckoutDetailsBean> convertToCheckoutDetails(List<CheckoutDetailDTO> details) {
        return details.stream()
            .map(dto -> {
                CheckoutDetailsBean detail = new CheckoutDetailsBean();
                detail.setCheckoutId(dto.getCheckoutId());
                detail.setProductId(dto.getProductId());
                detail.setNumberOfCheckout(dto.getQuantity());
                detail.setProductPrice(dto.getPrice());
                detail.setCheckoutPrice(dto.getSubtotal());
                return detail;
            })
            .collect(Collectors.toList());
    }
    
    private Map<String, Object> convertProductToMap(ProductBean product) {
        Map<String, Object> result = new HashMap<>();
        result.put("productId", product.getProductId());
        result.put("productName", product.getProductName());
        result.put("productPrice", product.getProductPrice());
        result.put("numberOfInventory", product.getNumberOfInventory());
        result.put("safeInventory", product.getProductSafeInventory());
        
        if (product.getProductPhoto() != null) {
            String base64Image = Base64.getEncoder().encodeToString(product.getProductPhoto());
            result.put("productImage", "data:image/jpeg;base64," + base64Image);
        }
        
        return result;
    }
    
 // 在 ShoppingCartController.java 中新增
    @PostMapping("/getAllProduct/json")
    @ResponseBody
    public ResponseEntity<Page<ProductBean>> getAllProductJson(
        @RequestParam(defaultValue = "1") Integer pageNumber,
        @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<ProductBean> products = productService.findAllProduct(pageNumber, pageSize);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            logger.severe("獲取商品列表失敗: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/add-checkout")
    @ResponseBody
    public ResponseEntity<?> addCheckout(@RequestBody Map<String, Object> checkoutData) {
        try {
            String checkoutId = (String) checkoutData.get("checkoutId");
            String customerTel = (String) checkoutData.get("customerTel");
            String employeeId = (String) checkoutData.get("employeeId");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cartItems = (List<Map<String, Object>>) checkoutData.get("items");

            // 創建結帳紀錄
            CheckoutBean checkout = new CheckoutBean();
            checkout.setCheckoutId(checkoutId);
            checkout.setCustomerTel(customerTel);
            checkout.setEmployeeId(employeeId);
            checkout.setCheckoutDate(new Date());
            checkout.setInvoiceNumber(checkoutService.generateNextInvoiceNumber());
            checkout.setCheckoutStatus("正常");

            // 轉換購物車項目為結帳明細
            List<CheckoutDetailsBean> details = cartItems.stream()
                .map(item -> {
                    CheckoutDetailsBean detail = new CheckoutDetailsBean();
                    detail.setCheckoutId(checkoutId);
                    detail.setProductId((String) item.get("productId"));
                    detail.setNumberOfCheckout(((Number) item.get("quantity")).intValue());
                    detail.setProductPrice(((Number) item.get("price")).intValue());
                    return detail;
                })
                .collect(Collectors.toList());

            boolean success = checkoutService.processCartCheckout(checkout, details);

            if (success) {
                return ResponseEntity.ok()
                    .body(Map.of("status", "success", 
                               "message", "結帳成功", 
                               "checkoutId", checkoutId));
            } else {
                return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", 
                               "message", "結帳處理失敗"));
            }
        } catch (Exception e) {
            logger.severe("處理結帳失敗: " + e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("status", "error", 
                           "message", "處理結帳失敗: " + e.getMessage()));
        }
    }
    
}
