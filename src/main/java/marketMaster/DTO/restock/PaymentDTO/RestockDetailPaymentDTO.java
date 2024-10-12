package marketMaster.DTO.restock.PaymentDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestockDetailPaymentDTO {
    private String detailId;
    private String supplierProductId;
    private int numberOfRestock;
    private int priceAtRestock;
    private int restockTotalPrice;
    private int paidAmount;
    private String supplierId;

}
