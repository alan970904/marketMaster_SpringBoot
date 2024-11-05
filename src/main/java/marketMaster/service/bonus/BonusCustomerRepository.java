//更名CustomerPointsRepository
//package marketMaster.service.bonus;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//import marketMaster.bean.customer.CustomerBean;
//
//@Repository
//public interface BonusCustomerRepository extends JpaRepository<CustomerBean, String> {
//
//    @Query("SELECT c.totalPoints FROM CustomerBean c WHERE c.customerTel = :customerTel")
//    int getCustomerPoints(@Param("customerTel") String customerTel);
//
//    @Modifying
//    @Transactional
//    @Query("UPDATE CustomerBean c SET c.totalPoints = :newPoints WHERE c.customerTel = :customerTel")
//    void updateCustomerPoints(@Param("customerTel") String customerTel, @Param("newPoints") int newPoints);
//}