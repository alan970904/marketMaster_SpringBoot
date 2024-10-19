package marketMaster.controller.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import marketMaster.DTO.product.InventoryCheckInsertDTO;
import marketMaster.bean.product.InventoryCheckBean;
import marketMaster.bean.product.ProductBean;
import marketMaster.service.product.InventoryCheckService;
import marketMaster.service.product.ProductService;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class InventoryCheckController {

	@Autowired
	private InventoryCheckService inventoryCheckService;
	
	@Autowired
	private ProductService productService;

	@GetMapping("/inventoryCheck/getAllProduct")
	public String getAllProduct(@RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
			@RequestParam(value = "size", defaultValue = "3") Integer pageSize, Model m) {
		Page<ProductBean> products = productService.findAllProduct(pageNumber, pageSize);
		List<InventoryCheckBean> inventoryCheck = inventoryCheckService.findAllInventoryCheck();
		m.addAttribute("products", products);
		m.addAttribute("inventoryCheck", inventoryCheck);
		return "/product/test";
	}
	
	
	@ResponseBody
	@GetMapping("/inventoryCheck/findByCheckId")
	public InventoryCheckBean findDetailsByCheckId(String inventoryCheckId) {
		InventoryCheckBean details = inventoryCheckService.findDetailsBycheckId(inventoryCheckId);
		return details;

	}
	

	@PostMapping("/inventoryCheck/addCheck")
	public void addInventoryCheck(@RequestBody InventoryCheckInsertDTO inventoryCheckInsertDTO) {
		inventoryCheckService.addInventoryCheck(inventoryCheckInsertDTO);
		
//		return null;
	}
	
	@PostMapping("/inventoryCheck/delete")
	public String deleteInventoryCheck(@RequestParam String inventoryCheckId) {
		inventoryCheckService.deleteInventoryCheck(inventoryCheckId);
		return null;
	}
	
}
