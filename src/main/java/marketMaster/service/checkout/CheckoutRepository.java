package marketMaster.service.checkout;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import marketMaster.bean.checkout.CheckoutBean;
import marketMaster.bean.checkout.CheckoutDetailsBean;
import marketMaster.bean.employee.EmpBean;
import marketMaster.bean.product.ProductBean;

import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

@Repository
public interface CheckoutRepository extends JpaRepository<CheckoutBean, String> {

    // 根據客戶電話搜索結帳記錄
    @Query("from CheckoutBean where customerTel like %:tel%")
    List<CheckoutBean> searchByTel(@Param("tel") String customerTel);

    // 更新結帳總價
    @Query("UPDATE CheckoutBean c SET c.checkoutTotalPrice = (SELECT SUM(cd.checkoutPrice) FROM CheckoutDetailsBean cd WHERE cd.id.checkoutId = :checkoutId) WHERE c.checkoutId = :checkoutId")
    void updateTotalPrice(@Param("checkoutId") String checkoutId);

    // 獲取每日銷售報告
    @Query("SELECT c.checkoutDate as checkoutDate, SUM(c.checkoutTotalPrice) as totalSales FROM CheckoutBean c GROUP BY c.checkoutDate")
    List<Map<String, Object>> getDailySalesReport();

    // 獲取結帳總表
    @Query("SELECT c.checkoutDate, c.employeeId, c.checkoutId, cd.productId, cd.numberOfCheckout, cd.checkoutPrice, c.checkoutTotalPrice FROM CheckoutBean c JOIN c.checkoutDetails cd")
    List<Map<String, Object>> getCheckoutSummary();

    // 獲取最新結帳ID
    @Query("SELECT c.checkoutId FROM CheckoutBean c ORDER BY c.checkoutId DESC")
    List<String> getLastCheckoutId();

    // 獲取所有員工
    @Query("FROM EmpBean")
    List<EmpBean> getAllEmployees();

    // 根據類別獲取產品名稱
    @Query("FROM ProductBean WHERE productCategory = :category")
    List<ProductBean> getProductNamesByCategory(@Param("category") String category);

    // 插入結帳記錄和明細（需要在服務層實現）

    // 刪除結帳記錄及其相關明細（需要在服務層實現）

    // 更新總金額和紅利點數
    @Query("UPDATE CheckoutBean c SET c.checkoutTotalPrice = :totalAmount, c.bonusPoints = :bonusPoints WHERE c.checkoutId = :checkoutId")
    void updateTotalAndBonus(@Param("checkoutId") String checkoutId, @Param("totalAmount") BigDecimal totalAmount, @Param("bonusPoints") int bonusPoints);
}