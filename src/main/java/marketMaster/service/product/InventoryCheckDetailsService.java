package marketMaster.service.product;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import marketMaster.bean.product.InventoryCheckDetailsBean;

@Service
public class InventoryCheckDetailsService {

	@Autowired
	private InventoryCheckDetailsRepository inventoryCheckDetailsRepo;

	public InventoryCheckDetailsBean findOneInventoryCheckDetailById(String detailId) {
		Optional<InventoryCheckDetailsBean> optional = inventoryCheckDetailsRepo.findById(detailId);

		if (optional.isPresent()) {
			InventoryCheckDetailsBean inventoryCheckDetail = optional.get();
			return inventoryCheckDetail;
		}
		return null;
	}

	public List<InventoryCheckDetailsBean> findByInventoryCheckDetailId(String inventoryCheckId) {

		return inventoryCheckDetailsRepo.findDetailByInventoryCheckId(inventoryCheckId);
	}

	public boolean addInventoryCheckDetail(InventoryCheckDetailsBean inventoryCheckDetailsBean) {
		boolean insertSuccess = false;
		String detailId = inventoryCheckDetailsBean.getDetailId();
		Optional<InventoryCheckDetailsBean> optional = inventoryCheckDetailsRepo.findById(detailId);
		if (optional.isEmpty()) {
			inventoryCheckDetailsRepo.save(inventoryCheckDetailsBean);
			insertSuccess = true;
			return insertSuccess;
		}
		return insertSuccess;
	}
	
	public boolean findCheckStatus(String inventoryCheckId) {
		List<Integer> differentialInventoryByCheckId = inventoryCheckDetailsRepo.findDifferentialInventoryByCheckId(inventoryCheckId);
		System.out.println(differentialInventoryByCheckId);
		boolean hasNonZero = differentialInventoryByCheckId.stream().anyMatch(n -> n != 0);
		return hasNonZero;
	}
	
	@Transactional
	public void updateDetailById(String detailId,Integer actualInventory,String remark) {
		Optional<InventoryCheckDetailsBean> optional = inventoryCheckDetailsRepo.findById(detailId);
		
		if (optional.isPresent()) {
			InventoryCheckDetailsBean detail = optional.get();
			detail.setActualInventory(actualInventory);
			detail.setDifferentialInventory(actualInventory-detail.getCurrentInventory());
			detail.setRemark(remark);
		}
	}
	
	
	public void deleteDetailById(String detailId) {
		inventoryCheckDetailsRepo.deleteById(detailId);
	}
	
	
	public boolean isNewestDetailId(String productId,String OutSideDetailId) {
		boolean isNewest =false;
		Optional<InventoryCheckDetailsBean> optional = inventoryCheckDetailsRepo.findFirstByProduct_ProductIdOrderByDetailIdDesc(productId);
		
		InventoryCheckDetailsBean details = optional.orElse(null);
		if (details.getDetailId().equals(OutSideDetailId) ) {
			isNewest = true;
		}
		
		return isNewest;
	}
	
	//自動生成新的明細id
	public String newDetailId() {

		String maxId = inventoryCheckDetailsRepo.findMaxId();
		if (maxId == null || maxId.isEmpty()) {
			return "ICD00000001";
		}
		String eng = maxId.substring(0, 3);
		String numStr = maxId.substring(3);
		int num = Integer.parseInt(numStr);
		String result = eng + String.format("%08d", num + 1);
		return result;
	}

}
