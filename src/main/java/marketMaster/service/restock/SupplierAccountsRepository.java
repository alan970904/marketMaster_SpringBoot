package marketMaster.service.restock;

import marketMaster.bean.restock.SupplierAccountsBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface SupplierAccountsRepository extends JpaRepository<SupplierAccountsBean,String> {
    Optional<SupplierAccountsBean> findBySupplier_SupplierId(String supplierId);

    @Modifying
    @Transactional
    @Query("UPDATE SupplierAccountsBean s SET s.totalAmount = :newTotalAmount WHERE s.supplier.supplierId = :supplierId")
    void updateSupplierTotalAmount(@Param("supplierId") String supplierId, @Param("newTotalAmount") int newTotalAmount);


        //透過supplierId找到accountId
        @Query("SELECT sa.accountId FROM SupplierAccountsBean sa where sa.supplier.supplierId=:supplierId")
        String findAccountIdBySupplierId(@Param("supplierId")String supplierId);

        //更新paid_amount
        @Modifying
        @Query("UPDATE SupplierAccountsBean sa set sa.paidAmount=:newPaidAmount ,sa.unpaidAmount = sa.totalAmount - :newPaidAmount  WHERE sa.supplier.supplierId=:supplierId")
        void updatePaidAmount(@Param("supplierId") String supplierId, @Param("newPaidAmount") int newPaidAmount);

        //加總所有supplierId下的付款總金額
        @Query("SELECT SUM(pr.paymentAmount) FROM PaymentRecordsBean pr WHERE pr.restockDetails.supplier.supplierId=:supplierId")
        Integer calculatePaidAmount(@Param("supplierId") String supplierId);
}
