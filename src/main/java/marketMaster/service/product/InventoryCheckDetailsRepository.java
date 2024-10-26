package marketMaster.service.product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import marketMaster.DTO.product.ICDDiffInventoryDTO;
import marketMaster.bean.product.InventoryCheckDetailsBean;

@Repository
public interface InventoryCheckDetailsRepository extends JpaRepository<InventoryCheckDetailsBean, String> {

	@Query("SELECT icd FROM InventoryCheckDetailsBean icd WHERE icd.inventoryCheck.inventoryCheckId = :inventoryCheckId")
	List<InventoryCheckDetailsBean> findDetailByInventoryCheckId(@Param("inventoryCheckId") String inventoryCheckId);
	
	@Query("SELECT MAX(icd.detailId) FROM InventoryCheckDetailsBean icd")
	String findMaxId();
	
	@Query("SELECT DISTINCT icd.differentialInventory FROM InventoryCheckDetailsBean icd WHERE icd.inventoryCheck.inventoryCheckId= :inventoryCheckId")
	List<Integer> findDifferentialInventoryByCheckId(@Param("inventoryCheckId") String inventoryCheckId);
 
	Optional<InventoryCheckDetailsBean> findFirstByProduct_ProductIdOrderByDetailIdDesc(String productId);

	@Query("SELECT EXISTS (SELECT 1 FROM InventoryCheckDetailsBean icd WHERE icd.product.productId = :productId AND icd.inventoryCheck.verifyStatus = :verifyStatus)")
	boolean findVerifyByProductId(@Param("productId") String productId ,@Param("verifyStatus") boolean verifyStatus);
	
}
