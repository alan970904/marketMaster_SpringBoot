package marketMaster.service.product;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import marketMaster.bean.product.InventoryCheckBean;
import marketMaster.bean.product.InventoryCheckDetailsBean;

@Service
public class InventoryCheckService {

	@Autowired
	private InventoryCheckRepository inventoryCheckRepo;

	@Autowired
	private InventoryCheckDetailsRepository inventoryCheckDetailsRepo;

	public InventoryCheckBean findDetailsBycheckId(String inventoryCheckId) {
		Optional<InventoryCheckBean> optional = inventoryCheckRepo.findById(inventoryCheckId);
		if (optional.isPresent()) {
			InventoryCheckBean inventoryCheck = optional.get();
			return inventoryCheck;
		}
		return null;
	}
	
	public List<InventoryCheckBean> findAllInventoryCheck() {
		List<InventoryCheckBean> inventoryChecks = inventoryCheckRepo.findAll();
		return inventoryChecks;
	}

	public InventoryCheckBean addInventoryCheck(InventoryCheckBean inventoryCheckBean) {
		String inventoryCheckId = inventoryCheckBean.getInventoryCheckId();
		Optional<InventoryCheckBean> optional = inventoryCheckRepo.findById(inventoryCheckId);
		LocalDate now = LocalDate.now();
		if (optional.isEmpty()) {
			InventoryCheckBean inventoryCheck = optional.get();
			inventoryCheck.setInventoryCheckDate(now);
			inventoryCheckRepo.save(inventoryCheck);
			List<InventoryCheckDetailsBean> detailsList = inventoryCheck.getDetails();
			for (InventoryCheckDetailsBean detail : detailsList) {
				inventoryCheckDetailsRepo.save(detail);
			}

		}
		return null;
	}

	public InventoryCheckBean updateInventoryCheck(InventoryCheckBean inventoryCheckBean) {
		String inventoryCheckId = inventoryCheckBean.getInventoryCheckId();
		Optional<InventoryCheckBean> optional = inventoryCheckRepo.findById(inventoryCheckId);

		if (optional.isPresent()) {
			InventoryCheckBean oldInventoryCheck = optional.get();
			return null;
		}
		return null;
	}

	public void deleteInventoryCheck(String inventoryCheckId) {
		inventoryCheckRepo.deleteById(inventoryCheckId);
	}

	public String newId() {

		String maxId = inventoryCheckDetailsRepo.findMaxId();
		if (maxId == null || maxId.isEmpty()) {
			return "IC001";
		}
		String eng = maxId.substring(0, 3);
		String numStr = maxId.substring(3);
		int num = Integer.parseInt(numStr);
		String result = eng + String.format("%03d", num + 1);
		return result;
	}
}
