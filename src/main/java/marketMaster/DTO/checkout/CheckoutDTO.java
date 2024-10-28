package marketMaster.DTO.checkout;

import java.util.Date;
import java.util.List;

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
	
	private String checkoutId;           // 新增：結帳編號
	
    private String invoiceNumber;        // 新增：發票號碼
    
    private String customerTel;          // 新增：顧客手機
    
    private String employeeId;           // 新增：員工編號
    
    private int totalAmount;            // 新增：總金額
    
    private int bonusPoints;            // 新增：紅利點數
    
    private String checkoutStatus;      // 新增：結帳狀態
    
    private List<CheckoutDetailDTO> details; // 新增：結帳明細列表
    
    // 新增給 ReturnProductRepository 使用的構造函數
    public CheckoutDTO(String productId, String productName, int productPrice, 
                      boolean isPerishable, Date checkoutDate, int numberOfCheckout, 
                      String checkoutId) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.isPerishable = isPerishable;
        this.checkoutDate = checkoutDate;
        this.numberOfCheckout = numberOfCheckout;
        this.originalCheckoutId = checkoutId;  // 用於退貨時參考原始結帳ID
    }
}
