package marketMaster.service.bonus;

import marketMaster.bean.bonus.PointsHistoryBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointsHistoryRepository extends JpaRepository<PointsHistoryBean, Integer> {
    List<PointsHistoryBean> findByCustomerTel(String customerTel);
}