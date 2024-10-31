package marketMaster.service.bonus;

import marketMaster.bean.customer.CustomerBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CustomerPointsRepository extends JpaRepository<CustomerBean, String> {
    // 更新客戶積分
    @Modifying
    @Transactional
    @Query("UPDATE CustomerBean c SET c.totalPoints = :newPoints WHERE c.customerTel = :customerTel")
    void updateCustomerPoints(@Param("customerTel") String customerTel, @Param("newPoints") int newPoints);
}
//JPA內建 findById方法取代
//原本查詢客戶積分
//@Query("SELECT c.totalPoints FROM CustomerBean c WHERE c.customerTel = :customerTel")
//int getCustomerPoints(@Param("customerTel") String customerTel);