package marketMaster.dao.checkout;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import marketMaster.bean.checkout.CheckoutDetailsBean;
import marketMaster.bean.checkout.CheckoutBean;

import java.util.List;
import java.util.Map;

@Repository
public interface CheckoutDetailsDao extends JpaRepository<CheckoutDetailsBean, CheckoutDetailsBean.CheckoutDetailsId> {

    // 獲取特定結帳ID的所有明細
    List<CheckoutDetailsBean> findByCheckoutId(String checkoutId);

    // 搜尋特定產品ID的結帳明細
    @Query("FROM CheckoutDetailsBean cd WHERE cd.productId LIKE %:productId%")
    List<CheckoutDetailsBean> searchByProductId(@Param("productId") String productId);

    // 計算商品退貨率
    @Query("SELECT cd.productId as productId, CAST(ROUND((SUM(CASE WHEN rd.returnId IS NOT NULL THEN rd.numberOfReturn ELSE 0 END) * 100.0) / SUM(cd.numberOfCheckout), 2) AS java.math.BigDecimal) AS returnRate " +
           "FROM CheckoutDetailsBean cd LEFT JOIN cd.returnDetails rd " +
           "GROUP BY cd.productId")
    List<Map<String, Object>> getProductReturnRates();

    // 計算結帳總金額
    @Query("SELECT COALESCE(CAST(SUM(cd.checkoutPrice) AS int), 0) FROM CheckoutDetailsBean cd WHERE cd.checkoutId = :checkoutId")
    int calculateCheckoutTotal(@Param("checkoutId") String checkoutId);

    // 更新結帳總金額和紅利點數（需要在服務層實現）

    // 更新退貨後的結帳明細（需要在服務層實現）

    // 取消退貨並更新結帳明細（需要在服務層實現）
}