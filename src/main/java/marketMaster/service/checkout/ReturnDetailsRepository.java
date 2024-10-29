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
    @Query("SELECT rd.returnId, rd.originalCheckoutId, rd.productId, rd.numberOfReturn, rd.returnPrice, " +
           "cd.numberOfCheckout, cd.checkoutPrice, rd.reasonForReturn " +
           "FROM ReturnDetailsBean rd JOIN rd.checkoutDetail cd")
    List<Map<String, Object>> getReturnComparisonTable();

    // 計算商品退貨率
    @Query("SELECT cd.productId as productId, " +
           "CAST(ROUND((SUM(CASE WHEN rd.returnId IS NOT NULL THEN rd.numberOfReturn ELSE 0 END) * 100.0) / NULLIF(SUM(cd.numberOfCheckout), 0), 2) AS java.math.BigDecimal) AS returnRate " +
           "FROM CheckoutDetailsBean cd " +
           "LEFT JOIN cd.returnDetails rd " +
           "GROUP BY cd.productId")
    List<Map<String, Object>> getProductReturnRates();

    // 獲取特定退貨ID的所有明細
    List<ReturnDetailsBean> findByReturnId(String returnId);

    // 根據商品ID搜索退貨明細
    @Query("FROM ReturnDetailsBean rd WHERE rd.productId LIKE %:productId%")
    List<ReturnDetailsBean> searchByProductId(@Param("productId") String productId);

    // 更新退貨狀態
    @Modifying
    @Query("UPDATE ReturnDetailsBean rd SET rd.returnStatus = :status WHERE rd.returnId = :returnId AND rd.originalCheckoutId = :checkoutId AND rd.productId = :productId")
    void updateReturnStatus(@Param("returnId") String returnId, @Param("checkoutId") String checkoutId, @Param("productId") String productId, @Param("status") String status);

    // 計算退貨總金額
    @Query("SELECT COALESCE(SUM(rd.returnPrice), 0) FROM ReturnDetailsBean rd WHERE rd.returnId = :returnId")
    int calculateReturnTotal(@Param("returnId") String returnId);

    // 刪除特定退貨ID的所有明細
    @Modifying
    @Query("DELETE FROM ReturnDetailsBean rd WHERE rd.returnId = :returnId")
    void deleteByReturnId(@Param("returnId") String returnId);

    // 獲取特定結帳ID和商品ID的退貨明細
    List<ReturnDetailsBean> findByOriginalCheckoutIdAndProductId(String originalCheckoutId, String productId);

    // 獲取特定商品的總退貨量
    @Query("SELECT COALESCE(SUM(rd.numberOfReturn), 0) FROM ReturnDetailsBean rd WHERE rd.productId = :productId")
    Integer getTotalReturnsByProduct(@Param("productId") String productId);

    // 獲取退貨原因統計
    @Query("SELECT rd.reasonForReturn, COUNT(rd) as count FROM ReturnDetailsBean rd GROUP BY rd.reasonForReturn")
    List<Map<String, Object>> getReturnReasonStatistics();

    // 根據原始結帳ID獲取退貨明細
    List<ReturnDetailsBean> findByOriginalCheckoutId(String originalCheckoutId);

    // 獲取特定時間段內的退貨明細
    @Query("FROM ReturnDetailsBean rd WHERE rd.returnProduct.returnDate BETWEEN :startDate AND :endDate")
    List<ReturnDetailsBean> findReturnDetailsByDateRange(@Param("startDate") java.util.Date startDate, @Param("endDate") java.util.Date endDate);

    // 獲取特定商品在特定時間段內的退貨數量
    @Query("SELECT COALESCE(SUM(rd.numberOfReturn), 0) FROM ReturnDetailsBean rd WHERE rd.productId = :productId AND rd.returnProduct.returnDate BETWEEN :startDate AND :endDate")
    Integer getProductReturnQuantityInDateRange(@Param("productId") String productId, @Param("startDate") java.util.Date startDate, @Param("endDate") java.util.Date endDate);
}