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
}
