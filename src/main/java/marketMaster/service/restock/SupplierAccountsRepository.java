package marketMaster.service.restock;

import marketMaster.bean.restock.SupplierAccountsBean;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupplierAccountsRepository extends JpaRepository<SupplierAccountsBean,String> {
    Optional<SupplierAccountsBean> findBySupplier_SupplierId(String supplierId);

}
