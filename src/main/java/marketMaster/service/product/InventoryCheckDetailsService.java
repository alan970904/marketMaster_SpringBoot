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
	public List<InventoryCheckDetailsBean> addDetails(InventoryCheckDetailsBean inventoryCheckDetailsBean) {
		return null;
	}
}
