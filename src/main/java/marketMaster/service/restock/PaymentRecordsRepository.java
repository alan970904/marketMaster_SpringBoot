package marketMaster.service.restock;

import marketMaster.bean.restock.PaymentRecordsBean;
import marketMaster.bean.restock.PaymentsBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PaymentRecordsRepository extends JpaRepository<PaymentRecordsBean,String> {

    //
    @Query("SELECT SUM(pr.paymentAmount) FROM PaymentRecordsBean pr WHERE pr.restockDetails.detailId = :detailId")
    Integer sumPaymentAmountByDetailId(String detailId);

    PaymentRecordsBean findTopByOrderByRecordIdDesc();

}
