package marketMaster.DTO.checkout;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutDetailDTO {
    private String checkoutId;
    private String productId;
    private String productName;
    private int quantity;
    private int price;
    private int subtotal;
    
    public void calculateSubtotal() {
        this.subtotal = this.quantity * this.price;
    }
}