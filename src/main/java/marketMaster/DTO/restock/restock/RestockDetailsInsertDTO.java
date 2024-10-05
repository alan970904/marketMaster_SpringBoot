package marketMaster.DTO.restock.restock;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestockDetailsInsertDTO {
    private String supplierId;
    private String supplierProductId;
    private int numberOfRestock;
    private int restockTotalPrice;
    private LocalDate productionDate;
    private LocalDate dueDate;
    private String productId;


}