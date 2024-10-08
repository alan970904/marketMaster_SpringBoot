package marketMaster.controller.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import marketMaster.DTO.product.ProductCategoryDTO;
import marketMaster.bean.product.ProductBean;
import marketMaster.service.product.ProductService;


@Controller
public class ProductController {
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private ProductRestController productRestController;
	
	@GetMapping("/product/addPage")
	public String addProductPage(Model m) {
		List<ProductCategoryDTO> productCategory = productRestController.getProductCategory();

		m.addAttribute("categorys", productCategory);
		return "/product/insertProduct";
	}
	
	@Transactional
	@PostMapping("/product/add")
	public String addProduct(@ModelAttribute ProductBean product,Model m) {
		ProductBean newProduct = productService.addProduct(product);
		System.out.println(product.getProductCategory());
		if (newProduct == null) {
			m.addAttribute("errorMsg", "商品編號已存在");
			
			return "/product/insertProduct";
		}
		m.addAttribute("message", "新增商品資料成功");

		m.addAttribute("product", newProduct);
		return "/product/showChangePage";
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
		m.addAttribute("pages", products);
		
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
		
		m.addAttribute("message", "成功更新商品資料");
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
		
		m.addAttribute("message", "成功上架商品");
		m.addAttribute("product", newProduct);
		return "/product/showChangePage";
	}
	@Transactional
	@PostMapping("/product/remove")
	public String postMethodName(@RequestParam String productId) {
		productService.removeProduct(productId);

		return "redirect:/product/findAll";
	}
	
}
