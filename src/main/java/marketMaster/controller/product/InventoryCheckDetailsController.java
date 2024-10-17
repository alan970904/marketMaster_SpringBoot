package marketMaster.controller.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import marketMaster.bean.product.InventoryCheckDetailsBean;
import marketMaster.service.product.InventoryCheckDetailsService;

@Controller
public class InventoryCheckDetailsController {
	
	@Autowired
	private InventoryCheckDetailsService inventoryCheckDetailsService;
	
	@ResponseBody
	@GetMapping("/InventoryCheckDetails/findOne")
	public InventoryCheckDetailsBean findOneInventoryCheckDetail(@RequestParam String detailId) {
		InventoryCheckDetailsBean aInventoryCheckDetail = inventoryCheckDetailsService.findOneInventoryCheckDetailById(detailId);
		return aInventoryCheckDetail;
	}
	
	@ResponseBody
	@GetMapping("/InventoryCheckDetails/findByCheck")
	public List<InventoryCheckDetailsBean> findByInventoryCheck(@RequestParam String inventoryCheckId) {
		List<InventoryCheckDetailsBean> detailList = inventoryCheckDetailsService.findByInventoryCheckDetailId(inventoryCheckId);
		return detailList;
	}
}
