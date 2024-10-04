package marketMaster.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import marketMaster.bean.product.ProductBean;
import marketMaster.service.product.ProductService;


@Controller
public class ProductController {
	
	@Autowired
	private ProductService productService;
	
	@PostMapping("/product/add")
	public String addProduct(@RequestBody ProductBean product) {
		productService.addProduct(product);
		return null;
	}
	
	@GetMapping("/product/findOne")
	public String getOneProduct(@RequestParam String productId, Model m) {
		ProductBean product = productService.findOneProduct(productId);
		
		m.addAttribute("product",product);
		return "product/findOnePage";
	}
	
	@GetMapping("/product/findAll")
	public String getAllProduct(@RequestParam(value = "page",defaultValue = "1")Integer pageNumber,@RequestParam(value = "size",defaultValue = "10") Integer pageSize , Model m) {
				
		Page<ProductBean> products = productService.findAllProduct(pageNumber,pageSize);
		
		m.addAttribute("products", products);
		
		return "product/findAllPage";
	}
	
	
	@GetMapping("/product/getUpdate")
	public String getUpdateProduct(@RequestParam String productId, Model m) {
		ProductBean product = productService.findOneProduct(productId);
		
		m.addAttribute("product",product);
		return "product/getUpdatePage";
	}
	
	@Transactional
	@PostMapping("/product/update")
	public String updateProduct(@ModelAttribute ProductBean product, Model m) {
		ProductBean newProduct = productService.updateProduct(product);
		
		m.addAttribute("product", newProduct);
		return "/product/showChangePage";
	}
	
	@GetMapping("/product/getShelve")
	public String getShelve(@RequestParam String productId, Model m) {
		ProductBean product = productService.findOneProduct(productId);
		
		m.addAttribute("product",product);
		return "product/getShelvePage";
	}
	
	@Transactional
	@PostMapping("/product/shelve")
	public String shelveProduct(@RequestParam String productId,@RequestParam Integer numberOfShelve, Model m) {
		ProductBean newProduct = productService.shelveProduct(productId,numberOfShelve);
		
		m.addAttribute("product", newProduct);
		return "/product/showChangePage";
	}
	@Transactional
	@PostMapping("/product/remove")
	public String postMethodName(@RequestParam String productId) {
		ProductBean product = productService.removeProduct(productId);
		

		return "redirect:/product/findAll";
	}
	
}
