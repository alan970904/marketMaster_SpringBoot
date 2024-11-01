package marketMaster.service.bonus;

import marketMaster.bean.bonus.BonusExchangeBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BonusExchangeRepository extends JpaRepository<BonusExchangeBean, String> {

    // 查詢特定客戶的兌換記錄
    List<BonusExchangeBean> findByCustomerTel(String customerTel);

    // 根據兌換ID查找兌換記錄
    @Query("SELECT b FROM BonusExchangeBean b WHERE b.exchangeId = :exchangeId")
    Optional<BonusExchangeBean> findByExchangeId(@Param("exchangeId") String exchangeId);

    @Query("SELECT b.exchangeId FROM BonusExchangeBean b ORDER BY b.exchangeId DESC")
    List<String> findLastExchangeId();
}