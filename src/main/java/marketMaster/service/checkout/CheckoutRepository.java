package marketMaster.service.checkout;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import marketMaster.bean.checkout.CheckoutBean;
import marketMaster.bean.checkout.CheckoutDetailsBean;
import marketMaster.bean.employee.EmpBean;
import marketMaster.bean.product.ProductBean;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface CheckoutRepository extends JpaRepository<CheckoutBean, String> {

    // 根據客戶電話搜索結帳記錄
    @Query("FROM CheckoutBean WHERE customerTel LIKE %:tel%")
    List<CheckoutBean> searchByTel(@Param("tel") String customerTel);

    // 更新結帳總價
    @Modifying
    @Query("UPDATE CheckoutBean c SET c.checkoutTotalPrice = (SELECT COALESCE(SUM(cd.checkoutPrice), 0) FROM CheckoutDetailsBean cd WHERE cd.checkoutId = :checkoutId) WHERE c.checkoutId = :checkoutId")
    void updateTotalPrice(@Param("checkoutId") String checkoutId);

    // 獲取每日銷售報告
    @Query("SELECT c.checkoutDate as checkoutDate, SUM(c.checkoutTotalPrice) as totalSales FROM CheckoutBean c WHERE c.checkoutStatus = '正常' GROUP BY c.checkoutDate")
    List<Map<String, Object>> getDailySalesReport();

    // 獲取結帳總表
    @Query("SELECT c.checkoutDate, c.employeeId, c.checkoutId, cd.productId, cd.numberOfCheckout, cd.checkoutPrice, c.checkoutTotalPrice " +
           "FROM CheckoutBean c JOIN c.checkoutDetails cd WHERE c.checkoutStatus = '正常'")
    List<Map<String, Object>> getCheckoutSummary();

    // 獲取最新結帳ID
    @Query("SELECT c.checkoutId FROM CheckoutBean c ORDER BY c.checkoutId DESC")
    List<String> getLastCheckoutId();

    // 獲取所有員工(不包含離職)
    @Query("SELECT e FROM EmpBean e WHERE e.resigndate IS NULL")
    List<EmpBean> getAllEmployees();

    // 根據類別獲取產品名稱
    @Query("FROM ProductBean WHERE productCategory = :category")
    List<ProductBean> getProductNamesByCategory(@Param("category") String category);

    // 更新總金額和紅利點數
    @Modifying
    @Query("UPDATE CheckoutBean c SET c.checkoutTotalPrice = :totalAmount, c.bonusPoints = :bonusPoints WHERE c.checkoutId = :checkoutId")
    void updateTotalAndBonus(@Param("checkoutId") String checkoutId, @Param("totalAmount") int totalAmount, @Param("bonusPoints") Integer bonusPoints);
    
    // 根據日期範圍獲取結帳記錄
    @Query("FROM CheckoutBean c WHERE c.checkoutDate BETWEEN :startDate AND :endDate AND c.checkoutStatus = '正常'")
    List<CheckoutBean> getCheckoutsByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    // 更新結帳總金額（考慮退貨）
    @Modifying
    @Query("UPDATE CheckoutBean c SET c.checkoutTotalPrice = (SELECT COALESCE(SUM(cd.checkoutPrice), 0) FROM CheckoutDetailsBean cd WHERE cd.checkoutId = c.checkoutId) WHERE c.checkoutId = :checkoutId")
    void updateTotalPriceAfterReturn(@Param("checkoutId") String checkoutId);

    // 獲取特定日期的銷售總額
    @Query("SELECT COALESCE(SUM(c.checkoutTotalPrice), 0) FROM CheckoutBean c WHERE c.checkoutDate = :date AND c.checkoutStatus = '正常'")
    Integer getDailySalesTotal(@Param("date") Date date);

    // 更新結帳狀態
    @Modifying
    @Query("UPDATE CheckoutBean c SET c.checkoutStatus = :status WHERE c.checkoutId = :checkoutId")
    void updateCheckoutStatus(@Param("checkoutId") String checkoutId, @Param("status") String status);

    // 根據發票號碼查詢結帳記錄
    @Query("FROM CheckoutBean c WHERE c.invoiceNumber = :invoiceNumber")
    CheckoutBean findByInvoiceNumber(@Param("invoiceNumber") String invoiceNumber);
    
    // 獲取最新發票號碼
    @Query("SELECT c.invoiceNumber FROM CheckoutBean c ORDER BY c.invoiceNumber DESC")
    List<String> getLastInvoiceNumber();

    @Query("UPDATE CustomerBean c SET c.totalPoints = c.totalPoints + :points WHERE c.customerTel = :tel")
    @Modifying
    void addBonusPointsToCustomer(@Param("tel") String customerTel, @Param("points") int points);

    @Query("UPDATE CustomerBean c SET c.totalPoints = c.totalPoints - :points WHERE c.customerTel = :tel") 
    @Modifying
    void deductBonusPointsFromCustomer(@Param("tel") String customerTel, @Param("points") int points);

    @Query("SELECT COUNT(c) > 0 FROM CustomerBean c WHERE c.customerTel = :tel")
    boolean existsByCustomerTel(@Param("tel") String customerTel);
}