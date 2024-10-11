package marketMaster.service.checkout;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import marketMaster.bean.checkout.ReturnProductBean;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface ReturnProductRepository extends JpaRepository<ReturnProductBean, String> {

    // 更新退貨總金額
    @Modifying
    @Query("UPDATE ReturnProductBean rp SET rp.returnTotalPrice = (SELECT SUM(rd.returnPrice) FROM ReturnDetailsBean rd WHERE rd.returnId = :returnId) WHERE rp.returnId = :returnId")
    void updateTotalPrice(@Param("returnId") String returnId);

    // 獲取退貨總表
    @Query("SELECT rp.returnDate AS returnDate, rp.employeeId AS employeeId, rp.returnId AS returnId, " +
           "rd.productId AS productId, rd.numberOfReturn AS numberOfReturn, rd.returnPrice AS returnPrice, " +
           "rp.returnTotalPrice AS returnTotalPrice, rd.reasonForReturn AS reasonForReturn " +
           "FROM ReturnProductBean rp JOIN rp.returnDetails rd")
    List<Map<String, Object>> getReturnSummary();

    // 獲取每日退貨總金額報告
    @Query("SELECT rp.returnDate as returnDate, SUM(rp.returnTotalPrice) as totalReturns FROM ReturnProductBean rp GROUP BY rp.returnDate")
    List<Map<String, Object>> getDailyReturnReport();

    // 根據日期範圍獲取退貨記錄
    @Query("FROM ReturnProductBean rp WHERE rp.returnDate BETWEEN :startDate AND :endDate")
    List<ReturnProductBean> getReturnsByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    // 獲取最新退貨ID
    @Query("SELECT rp.returnId FROM ReturnProductBean rp ORDER BY rp.returnId DESC")
    List<String> getLastReturnId();

    // 根據員工ID獲取退貨記錄
    List<ReturnProductBean> findByEmployeeId(String employeeId);
}