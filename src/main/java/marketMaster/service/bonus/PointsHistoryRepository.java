package marketMaster.service.bonus;

import marketMaster.bean.bonus.PointsHistoryBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointsHistoryRepository extends JpaRepository<PointsHistoryBean, Integer> {
	List<PointsHistoryBean> findByCustomerTel(String customerTel);

	@Query("SELECT ph FROM PointsHistoryBean ph JOIN ph.customer c WHERE c.customerName LIKE %:customerName%")
	List<PointsHistoryBean> findByCustomerNameContaining(@Param("customerName") String customerName);
}