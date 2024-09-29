package marketMaster.controller.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import marketMaster.bean.product.ProductBean;
import marketMaster.service.product.ProductService;


@Controller
public class ProductController {
	
	@Autowired
	private ProductService productService;
	
	@PostMapping("/product/add")
	public String addProduct(@RequestParam ProductBean product) {
		productService.addProduct(product);
		return "product/page";
	}
	
	@GetMapping("/product/findOne")
	public String getOneProduct(@RequestParam String productId, Model m) {
		ProductBean product = productService.findOneProduct(productId);
		
		m.addAttribute("product",product);
		return "product/page";
	}
	
	@GetMapping("/product/findAll")
	public String getAllProduct(Model m) {
		List<ProductBean> products = productService.findAllProduct();
		
		m.addAttribute("products", products);
		
		return "product/page";
	}
	
	@PostMapping("/product/update")
	public String updateProduct(@ModelAttribute ProductBean product, Model m) {
		ProductBean newProduct = productService.updateProduct(product);
		
		m.addAttribute("product", newProduct);
		return "redirect:/product/findOne";
	}
	
	@PostMapping("/product/shelve")
	public String shelveProduct(@RequestParam String productId,@RequestParam Integer shelveNumber, Model m) {
		ProductBean newProduct = productService.shelveProduct(productId,shelveNumber);
		
		m.addAttribute("product", newProduct);
		return "redirect:/product/findOne";
	}
	
	@PostMapping("/product/remove")
	public String postMethodName(@RequestParam String productId,Model m) {
		ProductBean product = productService.removeProduct(productId);
		
		m.addAttribute("product", product);
		return "redirect:/product/findOne";
	}
	
}
