package marketMaster.controller.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import marketMaster.DTO.product.ProductDTO;
import marketMaster.DTO.product.ProductPageDTO;
import marketMaster.bean.product.ProductBean;
import marketMaster.service.product.ProductService;

@RestController
public class ProductRestController {

	@Autowired
	private ProductService productService;

	@GetMapping("/product/findAll/json")
	public ResponseEntity<ProductPageDTO> getAllProduct(
			@RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
			@RequestParam(value = "size", defaultValue = "10") Integer pageSize) {

		Page<ProductBean> products = productService.findAllProduct(pageNumber, pageSize);
		ProductPageDTO dto = new ProductPageDTO(products);
		return ResponseEntity.ok(dto);
	}

	@GetMapping("/product/findOne/json")
	public ProductBean getOneProduct(@RequestBody ProductBean productBean) {
		ProductBean product = productService.findOneProduct(productBean.getProductId());

		return product;
	}

	@PostMapping("/product/update/json")
	public ProductBean  updateProduct(@RequestBody ProductBean product) {
		ProductBean newProduct = productService.updateProduct(product);

//		m.addAttribute("product", newProduct);
		return newProduct;
	}

}
