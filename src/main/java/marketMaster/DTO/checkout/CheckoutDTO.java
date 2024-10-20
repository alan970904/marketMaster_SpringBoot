package marketMaster.DTO.checkout;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutDTO {

	private String productId;

	private String productName;

	private int productPrice;

	
	private boolean isPerishable;
	
	private Date checkoutDate;
	
	private int numberOfCheckout;
	
	private String originalCheckoutId;
}
