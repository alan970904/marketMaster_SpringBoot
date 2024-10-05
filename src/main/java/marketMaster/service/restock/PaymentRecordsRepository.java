package marketMaster.service.restock;

import marketMaster.bean.restock.PaymentRecordsBean;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRecordsRepository extends JpaRepository<PaymentRecordsBean,String> {

}
