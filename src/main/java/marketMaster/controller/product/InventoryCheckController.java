package marketMaster.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import marketMaster.bean.product.InventoryCheckBean;
import marketMaster.service.product.InventoryCheckService;

@Controller
public class InventoryCheckController {

	@Autowired
	private InventoryCheckService inventoryCheckService;

	@ResponseBody
	@GetMapping("/inventoryCheck/findByCheckId")
	public InventoryCheckBean findDetailsByCheckId(String inventoryCheckId) {
		InventoryCheckBean details = inventoryCheckService.findDetailsBycheckId(inventoryCheckId);
		return details;

	}
}
