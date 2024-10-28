package marketMaster.DTO.checkout;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private List<CartItemDTO> items;
    private int totalAmount;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemDTO {
        private String productId;
        private String productName;
        private int quantity;
        private int price;
        private int subtotal;
        
        public void calculateSubtotal() {
            this.subtotal = this.quantity * this.price;
        }
    }
    
    public void calculateTotal() {
        this.totalAmount = items.stream()
                .mapToInt(item -> item.getQuantity() * item.getPrice())
                .sum();
    }
    
    // 新增商品到購物車
    public void addItem(CartItemDTO item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        
        // 檢查是否已存在相同商品
        Optional<CartItemDTO> existingItem = items.stream()
                .filter(i -> i.getProductId().equals(item.getProductId()))
                .findFirst();
        
        if (existingItem.isPresent()) {
            // 更新現有商品數量
            CartItemDTO existing = existingItem.get();
            existing.setQuantity(existing.getQuantity() + item.getQuantity());
            existing.calculateSubtotal();
        } else {
            // 添加新商品
            item.calculateSubtotal();
            items.add(item);
        }
        
        // 重新計算總金額
        calculateTotal();
    }
    
    // 更新購物車商品數量
    public void updateItemQuantity(String productId, int quantity) {
        items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(quantity);
                    item.calculateSubtotal();
                    calculateTotal();
                });
    }
    
    // 移除購物車商品
    public void removeItem(String productId) {
        items.removeIf(item -> item.getProductId().equals(productId));
        calculateTotal();
    }
    
    // 清空購物車
    public void clear() {
        if (items != null) {
            items.clear();
        }
        totalAmount = 0;
    }
    
    // 檢查購物車是否為空
    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }
    
    // 獲取購物車商品總數
    public int getTotalItems() {
        if (items == null) {
            return 0;
        }
        return items.stream()
                .mapToInt(CartItemDTO::getQuantity)
                .sum();
    }
}