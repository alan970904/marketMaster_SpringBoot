package marketMaster.DTO.restock.PaymentDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRecordInsertDTO {
    private String detailId;
    private int paymentAmount;

}
