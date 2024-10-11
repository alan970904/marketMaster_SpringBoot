package marketMaster.service.restock;

import marketMaster.bean.restock.PaymentsBean;
import marketMaster.bean.restock.RestockDetailsBean;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentsRepository extends JpaRepository<PaymentsBean ,String> {
    PaymentsBean findTopByOrderByPaymentIdDesc();

}
