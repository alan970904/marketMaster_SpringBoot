package marketMaster.controller.product.front;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import marketMaster.DTO.product.InventoryCheckInsertDTO;
import marketMaster.DTO.product.ProductPageDTO;
import marketMaster.bean.product.InventoryCheckBean;
import marketMaster.bean.product.ProductBean;
import marketMaster.service.product.InventoryCheckService;
import marketMaster.service.product.ProductService;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FInventoryCheckController {

	@Autowired
	private InventoryCheckService inventoryCheckService;

	@Autowired
	private ProductService productService;

	@GetMapping("/inventoryCheck/front/home")
	public String homePage() {
		return "/product/front/checkHomePage";
	}

	@GetMapping("/inventoryCheck/front/getAllProduct")
	public String getAllProduct(@RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
			@RequestParam(value = "size", defaultValue = "10") Integer pageSize, Model m) {
		Page<ProductBean> products = productService.findAllProduct(pageNumber, pageSize);
		m.addAttribute("products", products);
		return "/product/front/checkHomePage";
	}

	@PostMapping("/inventoryCheck/front/addCheck")
	@ResponseBody
	public ResponseEntity<?> addInventoryCheck(@RequestBody InventoryCheckInsertDTO inventoryCheckInsertDTO) {
		ResponseEntity<?> response = inventoryCheckService.addInventoryCheck(inventoryCheckInsertDTO);
		return response;
	}

	@PostMapping("/inventoryCheck/front/delete")
	public String deleteInventoryCheck(@RequestParam String inventoryCheckId) {
		inventoryCheckService.deleteInventoryCheck(inventoryCheckId);
		return null;
	}
	
	@GetMapping("/inventoryCheck/front/findAll")
	public String findAllCheck(Model m) {
		List<InventoryCheckBean> inventoryChecks = inventoryCheckService.findAllInventoryCheck();
		for (InventoryCheckBean inventoryCheckBean : inventoryChecks) {
			System.out.println(inventoryCheckBean.getInventoryCheckId());
		}
		m.addAttribute("inventoryChecks", inventoryChecks);
		return "/product/front/inventoryChecksPage";
	}
	
	@ResponseBody
	@GetMapping("/inventoryCheck/front/update")
	public ResponseEntity<?> updateCheck(@RequestBody InventoryCheckBean inventoryCheck) {
		inventoryCheckService.updateInventoryCheck(inventoryCheck);
		return ResponseEntity.ok(null);
	}
	
	
	
	
}
