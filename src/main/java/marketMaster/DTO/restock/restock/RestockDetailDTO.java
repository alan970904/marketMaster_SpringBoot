package marketMaster.DTO.restock.restock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestockDetailDTO {
    private String restockId;
    private String detailId;
    private String supplierId;
    private String supplierName;
    private String productId;
    private String productName;
    private int numberOfRestock;
    private int priceAtRestock;
    private int productPrice;
    private int restockTotalPrice;
}