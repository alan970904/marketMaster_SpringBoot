package marketMaster.service.checkout;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import marketMaster.bean.checkout.ReturnDetailsBean;
import marketMaster.bean.checkout.ReturnDetailsBean.ReturnDetailsId;

import java.util.List;
import java.util.Map;

@Repository
public interface ReturnDetailsRepository extends JpaRepository<ReturnDetailsBean, ReturnDetailsId> {

    // 呈現每筆退貨數量及金額對應到先前結帳明細的數量及金額之比對表格
    @Query("SELECT rd.returnId, rd.checkoutId, rd.productId, rd.numberOfReturn, rd.returnPrice, " +
           "cd.numberOfCheckout, cd.checkoutPrice, rd.reasonForReturn " +
           "FROM ReturnDetailsBean rd JOIN CheckoutDetailsBean cd " +
           "ON rd.checkoutId = cd.checkoutId AND rd.productId = cd.productId")
    List<Map<String, Object>> getReturnComparisonTable();

    // 計算商品退貨率
    @Query("SELECT cd.productId as productId, " +
           "CAST(ROUND((SUM(CASE WHEN rd.returnId IS NOT NULL THEN rd.numberOfReturn ELSE 0 END) * 100.0) / SUM(cd.numberOfCheckout), 2) AS java.math.BigDecimal) AS returnRate " +
           "FROM CheckoutDetailsBean cd " +
           "LEFT JOIN ReturnDetailsBean rd ON cd.productId = rd.productId AND cd.checkoutId = rd.checkoutId " +
           "GROUP BY cd.productId")
    List<Map<String, Object>> getProductReturnRates();

    // 獲取特定退貨ID的所有明細
    List<ReturnDetailsBean> findByReturnId(String returnId);

    // 根據商品ID搜索退貨明細
    @Query("FROM ReturnDetailsBean rd WHERE rd.productId LIKE %:productId%")
    List<ReturnDetailsBean> searchByProductId(@Param("productId") String productId);

    // 更新退貨狀態
    @Modifying
    @Query("UPDATE ReturnDetailsBean rd SET rd.returnStatus = :status WHERE rd.returnId = :returnId AND rd.checkoutId = :checkoutId AND rd.productId = :productId")
    void updateReturnStatus(@Param("returnId") String returnId, @Param("checkoutId") String checkoutId, @Param("productId") String productId, @Param("status") String status);

    // 計算退貨總金額
    @Query("SELECT COALESCE(SUM(rd.returnPrice), 0) FROM ReturnDetailsBean rd WHERE rd.returnId = :returnId")
    int calculateReturnTotal(@Param("returnId") String returnId);

    // 刪除特定退貨ID的所有明細
    @Modifying
    @Query("DELETE FROM ReturnDetailsBean rd WHERE rd.returnId = :returnId")
    void deleteByReturnId(@Param("returnId") String returnId);
}