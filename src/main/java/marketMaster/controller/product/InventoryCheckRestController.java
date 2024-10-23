package marketMaster.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import marketMaster.DTO.product.ProductPageDTO;
import marketMaster.bean.product.InventoryCheckBean;
import marketMaster.bean.product.ProductBean;
import marketMaster.service.product.InventoryCheckService;
import marketMaster.service.product.ProductService;

@RestController
public class InventoryCheckRestController {
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private InventoryCheckService inventoryCheckService;
	
	@PostMapping("/inventoryCheck/getAllProduct/json")
	public Page<ProductBean> getAllProductJson(@RequestBody ProductPageDTO productPageDTO , Model m) {
		Integer pageSize = productPageDTO.getPageSize();
		Integer pageNumber = productPageDTO.getPageNumber();
		Page<ProductBean> products = productService.findAllProduct(pageNumber, pageSize);
		m.addAttribute("products", products);
		return products;
	}
	@PostMapping("/inventoryCheck/getProductByCategory/json")
	public Page<ProductBean> getProductByCategoryJson(@RequestBody ProductPageDTO productPageDTO , Model m) {
		Integer pageSize = productPageDTO.getPageSize();
		Integer pageNumber = productPageDTO.getPageNumber();
		String productCategory = productPageDTO.getProductCategory();
		Page<ProductBean> products = productService.findProductByCategory(productCategory,pageNumber, pageSize);
		m.addAttribute("products", products);
		return products;
	}
	
	@GetMapping("/inventoryCheck/findByCheckId")
	public InventoryCheckBean findDetailsByCheckId(String inventoryCheckId) {
		InventoryCheckBean details = inventoryCheckService.findDetailsBycheckId(inventoryCheckId);
		return details;

	}
	

	
}
