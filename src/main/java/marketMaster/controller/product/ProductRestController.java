package marketMaster.controller.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import marketMaster.DTO.product.ProductDTO;
import marketMaster.bean.product.ProductBean;
import marketMaster.service.product.ProductService;

@RestController
public class ProductRestController {
	
	@Autowired
	private ProductService productService;
	
	@GetMapping("/product/findAll/json")
	public List<ProductBean> getAllProduct() {
		List<ProductBean> products = productService.findAllProduct();
		
		
		return products;
	}
	
	@GetMapping("/product/findOne/json")
	public ProductBean getOneProduct(@RequestBody ProductBean productBean) {
		ProductBean product = productService.findOneProduct(productBean.getProductId());
		
		return product;	
		}
}
