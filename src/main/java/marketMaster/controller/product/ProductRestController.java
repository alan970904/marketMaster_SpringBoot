package marketMaster.controller.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import marketMaster.DTO.product.ProductCategoryDTO;
import marketMaster.DTO.product.ProductPageDTO;
import marketMaster.bean.product.ProductBean;
import marketMaster.service.product.ProductService;

@RestController
public class ProductRestController {

	@Autowired
	private ProductService productService;

	@PostMapping("/product/findProductByLike/json")
	public Page<ProductBean> getProductsByLike(@RequestBody ProductPageDTO productPageDTO){
		String productName = productPageDTO.getProductName();
		Integer pageNumber = productPageDTO.getPageNumber();
		Page<ProductBean> products = productService.findProductByLike(productName,pageNumber);
		
		return products;
	}
	
	
	@PostMapping("/product/findProductCategory")
	public List<ProductCategoryDTO> getProductCategory() {
		List<ProductCategoryDTO> productCategory = productService.findProductCategory();
		return productCategory;
	}
	
	

}
