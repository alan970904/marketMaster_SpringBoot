package marketMaster.service.restock;

import marketMaster.DTO.restock.SupplierDTO.SupplierIdAndNameDTO;
import marketMaster.DTO.restock.SupplierDTO.SupplierInfoDTO;
import marketMaster.bean.restock.SuppliersBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.function.Supplier;

public interface SuppliersRepository extends JpaRepository<SuppliersBean, String> {

    @Query("SELECT new marketMaster.DTO.restock.SupplierDTO.SupplierIdAndNameDTO(s.supplierId,s.supplierName)  from SuppliersBean s ")
    List<SupplierIdAndNameDTO> findAllSupplierIdAndName();

    @Query("SELECT new marketMaster.DTO.restock.SupplierDTO.SupplierInfoDTO(s.supplierId, s.supplierName, s.address, s.contactPerson, s.phone, s.email, sa.totalAmount, sa.paidAmount, sa.unpaidAmount) " +
            "FROM SuppliersBean s LEFT JOIN SupplierAccountsBean sa ON s.supplierId = sa.supplier.supplierId")
    List<SupplierInfoDTO> findAllSuppliersWithAccounts();

    //找到最大的supplierId
    @Query("SELECT MAX(s.supplierId) FROM SuppliersBean s")
    String getLastSupplierId();

}
