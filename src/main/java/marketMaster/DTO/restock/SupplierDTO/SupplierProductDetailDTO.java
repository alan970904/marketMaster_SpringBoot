package marketMaster.DTO.restock.SupplierDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplierProductDetailDTO {
    private String supplierProductId;
    private String productId;
    private String productName;
    private int productPrice;
    private int status;

}
