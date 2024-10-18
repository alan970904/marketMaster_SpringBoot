package marketMaster.service.product;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	public InventoryCheckDetailsBean addInventoryCheckDetail(InventoryCheckDetailsBean inventoryCheckDetailsBean) {
		String detailId = inventoryCheckDetailsBean.getDetailId();
		Optional<InventoryCheckDetailsBean> optional = inventoryCheckDetailsRepo.findById(detailId);
		if (optional.isEmpty()) {
			return inventoryCheckDetailsRepo.save(inventoryCheckDetailsBean);
		}
		return null;
	}
	
	public String newId() {
		
		String maxId = inventoryCheckDetailsRepo.findMaxId();
		if (maxId == null || maxId.isEmpty()) {
			return "ICD00000001";
		}
		String eng = maxId.substring(0, 3);
		String numStr = maxId.substring(3);
		int num = Integer.parseInt(numStr);
		String result = eng+String.format("%08d", num+1);
		return result;
	}

}
