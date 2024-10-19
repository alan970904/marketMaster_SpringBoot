package marketMaster.service.bonus;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import marketMaster.bean.customer.CustomerBean;

public interface PointsHistoryCustomerRepository extends JpaRepository<CustomerBean, String> {
    Optional<CustomerBean> findByCustomerTel(String customerTel);
    Optional<String> findCustomerNameByTel(String customerTel);
}