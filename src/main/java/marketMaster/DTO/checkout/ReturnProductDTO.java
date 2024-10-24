package marketMaster.DTO.checkout;

import lombok.Data;
import java.util.List;

@Data
public class ReturnProductDTO {
    private String returnId;
    private String originalInvoiceNumber;
    private String originalCheckoutId;
    private String employeeId;
    private Integer returnTotalPrice;
    private String returnDate;
    private List<ReturnDetailDTO> returnProducts;
}