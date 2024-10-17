package marketMaster.service.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import marketMaster.bean.product.InventoryCheckBean;

@Repository
public interface InventoryCheckRepository extends JpaRepository<InventoryCheckBean, String> {

}
