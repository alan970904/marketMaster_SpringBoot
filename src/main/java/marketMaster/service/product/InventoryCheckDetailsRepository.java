package marketMaster.service.product;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import marketMaster.bean.product.InventoryCheckDetailsBean;

@Repository
public interface InventoryCheckDetailsRepository extends JpaRepository<InventoryCheckDetailsBean, String> {

	@Query("SELECT icd FROM InventoryCheckDetailsBean icd WHERE icd.inventoryCheck.inventoryCheckId = :inventoryCheckId")
	List<InventoryCheckDetailsBean> findDetailByInventoryCheckId(@Param("inventoryCheckId") String inventoryCheckId);
	
	@Query("SELECT MAX(icd.detailId) FROM InventoryCheckDetailsBean icd")
	String findMaxId();
}
