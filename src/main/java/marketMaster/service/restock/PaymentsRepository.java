package marketMaster.service.restock;

import marketMaster.bean.restock.PaymentsBean;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentsRepository extends JpaRepository<PaymentsBean ,String> {
}
