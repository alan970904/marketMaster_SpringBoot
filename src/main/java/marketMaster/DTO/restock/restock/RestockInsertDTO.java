package marketMaster.DTO.restock.restock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestockInsertDTO {
    private String restockId;
    private int restockTotalPrice;
    private LocalDate restockDate;
    private String employeeId;
    private List<RestockDetailsInsertDTO> restockDetails;
}
