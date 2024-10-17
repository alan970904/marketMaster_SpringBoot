package marketMaster.service.product;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import marketMaster.bean.product.InventoryCheckBean;

@Service
public class InventoryCheckService {

	@Autowired
	private InventoryCheckRepository inventoryCheckRepo;
	
	public InventoryCheckBean findDetailsBycheckId(String inventoryCheckId) {
		Optional<InventoryCheckBean> optional = inventoryCheckRepo.findById(inventoryCheckId);
		if (optional.isPresent()) {
			InventoryCheckBean inventoryCheck = optional.get();
			return inventoryCheck;
		}
		return null;
	}
}
