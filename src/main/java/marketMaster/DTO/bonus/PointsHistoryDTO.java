package marketMaster.DTO.bonus;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointsHistoryDTO {
    private int pointsHistoryId;
    private String customerTel;
    private String checkoutId;
    private String exchangeId;
    private int pointsChange;
    private LocalDate transactionDate;
    private String transactionType;
    private String encryptedCustomerId;
    private String customerName;
}