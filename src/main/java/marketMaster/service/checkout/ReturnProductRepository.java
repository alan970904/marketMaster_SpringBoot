package marketMaster.service.checkout;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import marketMaster.DTO.checkout.CheckoutDTO;
import marketMaster.DTO.restock.restock.RestockDTO;
import marketMaster.bean.checkout.ReturnProductBean;
import marketMaster.bean.employee.EmpBean;
import marketMaster.bean.product.ProductBean;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface ReturnProductRepository extends JpaRepository<ReturnProductBean, String> {

    // 更新退貨總金額
    @Modifying
    @Query("UPDATE ReturnProductBean rp SET rp.returnTotalPrice = (SELECT COALESCE(SUM(rd.returnPrice), 0) FROM ReturnDetailsBean rd WHERE rd.returnId = :returnId) WHERE rp.returnId = :returnId")
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

    // 獲取所有員工
    @Query("SELECT e FROM EmpBean e")
    List<EmpBean> getAllEmployees();

    // 根據類別獲取產品名稱（用於退貨商品選擇）
    @Query("FROM ProductBean WHERE productCategory = :category")
    List<ProductBean> getProductNamesByCategory(@Param("category") String category);

    // 獲取特定日期的退貨總額
    @Query("SELECT COALESCE(SUM(rp.returnTotalPrice), 0) FROM ReturnProductBean rp WHERE rp.returnDate = :date")
    Integer getDailyReturnTotal(@Param("date") Date date);

    // 獲取退貨率最高的前N個商品
    @Query("SELECT rd.productId, COUNT(rd) as returnCount, " +
    	       "(SELECT COUNT(cd) FROM CheckoutDetailsBean cd WHERE cd.productId = rd.productId) as saleCount, " +
    	       "CAST(COUNT(rd) * 100.0 / NULLIF((SELECT COUNT(cd) FROM CheckoutDetailsBean cd WHERE cd.productId = rd.productId), 0) AS java.math.BigDecimal) as returnRate " +
    	       "FROM ReturnDetailsBean rd " +
    	       "GROUP BY rd.productId " +
    	       "ORDER BY returnRate DESC")
    	List<Map<String, Object>> getTopReturnRateProducts(Pageable pageable);

    // 根據原始結帳ID獲取退貨記錄
    List<ReturnProductBean> findByOriginalCheckoutId(String originalCheckoutId);

    @Query("SELECT c.checkoutId FROM CheckoutBean c WHERE c.invoiceNumber = :invoiceNumber")
    String getOriginalCheckoutIdByInvoiceNumber(@Param("invoiceNumber") String invoiceNumber);

    // 根據原始發票號碼獲取退貨記錄
    ReturnProductBean findByOriginalInvoiceNumber(String originalInvoiceNumber);

    // 獲取特定時間段內的退貨總額
    @Query("SELECT COALESCE(SUM(rp.returnTotalPrice), 0) FROM ReturnProductBean rp WHERE rp.returnDate BETWEEN :startDate AND :endDate")
    Integer getTotalReturnAmountInDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    // 獲取特定員工在特定時間段內處理的退貨總額
    @Query("SELECT COALESCE(SUM(rp.returnTotalPrice), 0) FROM ReturnProductBean rp WHERE rp.employeeId = :employeeId AND rp.returnDate BETWEEN :startDate AND :endDate")
    Integer getEmployeeReturnAmountInDateRange(@Param("employeeId") String employeeId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    // 根據發票號碼獲取商品資料
//    @Query(nativeQuery = true, value = 
//    	    "SELECT p.product_id, p.product_name, p.product_price, p.is_perishable AS isPerishable, c.checkout_date AS purchaseDate, cd.number_of_checkout AS quantity " +
//    	    "FROM checkout c " +
//    	    "JOIN checkout_details cd ON c.checkout_id = cd.checkout_id " +
//    	    "JOIN products p ON cd.product_id = p.product_id " +
//    	    "WHERE c.invoice_number = :invoiceNumber")
//    	List<Map<String, Object>> getInvoiceProducts(@Param("invoiceNumber") String invoiceNumber);
    
    @Query("SELECT new marketMaster.DTO.checkout.CheckoutDTO(c.product.productId, c.product.productName, c.product.productPrice, c.product.isPerishable, c.checkout.checkoutDate, c.numberOfCheckout, c.checkout.checkoutId) FROM CheckoutDetailsBean c WHERE c.checkout.invoiceNumber = :invoiceNumber")
    List<CheckoutDTO> getInvoiceProducts(@Param("invoiceNumber") String invoiceNumber);
    
    // 獲取在職員工（根據resigndate是否為null來判斷）
    @Query("SELECT e FROM EmpBean e WHERE e.resigndate IS NULL")
    List<EmpBean> getActiveEmployees();
    
}