package marketMaster.DTO.restock.PaymentDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentInsertDTO {
    private String accountId;
    private LocalDate paymentDate;
    private String paymentMethod;
    private int totalAmount;
    private String paymentStatus;
    List<PaymentRecordInsertDTO> paymentRecords;


}
