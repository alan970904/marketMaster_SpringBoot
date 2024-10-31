package marketMaster.DTO.bonus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//points_history & customer
//紅利紀錄＆會員

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPointsDTO {
    private String customerTel;
    private String customerName;
    private int points;
}