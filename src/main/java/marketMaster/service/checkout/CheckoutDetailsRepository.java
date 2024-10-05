package marketMaster.service.checkout;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import marketMaster.bean.checkout.CheckoutDetailsBean;
import marketMaster.bean.checkout.CheckoutDetailsBean.CheckoutDetailsId;

import java.util.List;
import java.util.Map;

@Repository
public interface CheckoutDetailsRepository extends JpaRepository<CheckoutDetailsBean, CheckoutDetailsId> {

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

    // 刪除特定結帳ID的所有明細
    @Modifying
    @Query("DELETE FROM CheckoutDetailsBean cd WHERE cd.checkoutId = :checkoutId")
    void deleteByCheckoutId(@Param("checkoutId") String checkoutId);

    // 更新退貨後的結帳明細
    @Modifying
    @Query("UPDATE CheckoutDetailsBean cd SET cd.numberOfCheckout = cd.numberOfCheckout - :returnQuantity, " +
           "cd.checkoutPrice = cd.checkoutPrice - :returnPrice " +
           "WHERE cd.checkoutId = :checkoutId AND cd.productId = :productId")
    void updateAfterReturn(@Param("checkoutId") String checkoutId, 
                           @Param("productId") String productId, 
                           @Param("returnQuantity") int returnQuantity, 
                           @Param("returnPrice") int returnPrice);

    // 取消退貨並更新結帳明細
    @Modifying
    @Query("UPDATE CheckoutDetailsBean cd SET cd.numberOfCheckout = cd.numberOfCheckout + :returnQuantity, " +
           "cd.checkoutPrice = cd.checkoutPrice + :returnPrice " +
           "WHERE cd.checkoutId = :checkoutId AND cd.productId = :productId")
    void cancelReturn(@Param("checkoutId") String checkoutId, 
                      @Param("productId") String productId, 
                      @Param("returnQuantity") int returnQuantity, 
                      @Param("returnPrice") int returnPrice);
}