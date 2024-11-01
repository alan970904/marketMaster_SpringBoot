package marketMaster.DTO.bonus;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
//bonus_exchange
//紅利兌換
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BonusExchangeDTO {
    private String exchangeId;
    private String customerTel;
    private String itemId;
    private String productName;
    private int usePoints;
    private int numberOfExchange;
    private LocalDate exchangeDate;
}