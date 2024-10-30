package marketMaster.controller.product;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import marketMaster.DTO.product.ProductCategoryDTO;
import marketMaster.DTO.product.ProductSalesAndReturnDTO;
import marketMaster.bean.product.ProductBean;
import marketMaster.service.product.ProductService;

@Controller
public class ProductController {

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRestController productRestController;
	
	@GetMapping("/product/test")
	public String testPage() {
		return "/product/test";
	}
	
	@GetMapping("/product/addPage")
	public String addProductPage(Model m) {
		List<ProductCategoryDTO> productCategory = productRestController.getProductCategory();

		m.addAttribute("categorys", productCategory);
		return "/product/insertProduct";
	}

	@Transactional
	@PostMapping("/product/add")
	public String addProduct(@ModelAttribute ProductBean product, @RequestParam MultipartFile photo, Model m)
			throws IOException {

		ProductBean newProduct = productService.addProduct(product, photo);
		System.out.println(product.getProductCategory());
		if (newProduct == null) {
			m.addAttribute("errorMsg", "商品編號已存在");

			return "/product/insertProduct";
		}
		m.addAttribute("message", "新增商品資料成功");

		m.addAttribute("product", newProduct);
		return "/product/showChangePage";
	}

	@GetMapping("/product/downloadProductPhoto")
	public ResponseEntity<?> getProductPhoto(@RequestParam String productId) {
		ProductBean product = productService.findOneProduct(productId);

		byte[] productPhotoByte = product.getProductPhoto();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);

		return new ResponseEntity<byte[]>(productPhotoByte, headers, HttpStatus.OK);
	}

	@GetMapping("/product/findOne")
	public String getOneProduct(@RequestParam String productId, Model m) {
		ProductBean product = productService.findOneProduct(productId);

		byte[] productPhotoByte = product.getProductPhoto();

		System.out.println(productPhotoByte);
		m.addAttribute("product", product);
		return "product/findOnePage";
	}

	@GetMapping("/product/findProductAvailable") // 測試中 先預設false
	public String getProductAvailable(@RequestParam(defaultValue = "false") boolean available,
			@RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
			@RequestParam(value = "size", defaultValue = "10") Integer pageSize, Model m) {

		Page<ProductBean> products = productService.findProductAvailable(available, pageNumber, pageSize);

		m.addAttribute("products", products);
		m.addAttribute("pages", products);

		return "product/findAllPage";
	}

	@GetMapping("/product/findProductInventoryNotEnough")
	public String getProductInventoryNotEnough(@RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
			@RequestParam(value = "size", defaultValue = "10") Integer pageSize, Model m) {
		Page<ProductBean> products = productService.findProductNotEnough(pageNumber, pageSize);

		m.addAttribute("products", products);
		m.addAttribute("pages", products);
		return "product/findAllPage";
	}

	@GetMapping("/product/findAll")
	public String getAllProduct(@RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
			@RequestParam(value = "size", defaultValue = "10") Integer pageSize, Model m) {

		Page<ProductBean> products = productService.findAllProduct(pageNumber, pageSize);

		m.addAttribute("products", products);
		m.addAttribute("pages", products);

		return "product/findAllPage";
	}

	@GetMapping("/product/getUpdate")
	public String getUpdateProduct(@RequestParam String productId, Model m) {
		ProductBean product = productService.findOneProduct(productId);

		m.addAttribute("product", product);
		return "product/getUpdatePage";
	}

	@Transactional
	@PostMapping("/product/update")
	public String updateProduct(@ModelAttribute ProductBean product, @RequestParam MultipartFile photo, Model m)
			throws IOException {
		ProductBean newProduct = productService.updateProduct(product,photo);
		m.addAttribute("message", "成功更新商品資料");
		m.addAttribute("product", newProduct);
		return "/product/showChangePage";
	}

	@GetMapping("/product/getphotopage")
	public String updatephotopage() {
		return "/product/getupdatephoto";
	}

	@Transactional
	@PostMapping("/product/updatephoto")
	public void updateProductPhoto(@RequestParam String productId, @RequestParam MultipartFile photo, Model m) {
		try {
			productService.updateProductPhoto(productId, photo);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@GetMapping("/product/getShelve")
	public String getShelve(@RequestParam String productId, Model m) {
		ProductBean product = productService.findOneProduct(productId);

		m.addAttribute("product", product);
		return "product/getShelvePage";
	}

	@Transactional
	@PostMapping("/product/shelve")
	public String shelveProduct(@RequestParam String productId, @RequestParam Integer numberOfShelve, Model m) {
		ProductBean newProduct = productService.shelveProduct(productId, numberOfShelve);

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
	
	//  ===============計算銷售率及退貨率用的=============
	
	@GetMapping("/product/statistics/sales-and-returns")
	@ResponseBody
	public ResponseEntity<Map<String, List<ProductSalesAndReturnDTO>>> getProductStatistics() {
	    List<ProductSalesAndReturnDTO> allStats = productService.getProductSalesAndReturnStats();
	    
	    Map<String, List<ProductSalesAndReturnDTO>> result = new HashMap<>();
	    result.put("topSales", allStats.stream()
	        .sorted((a, b) -> Long.compare(b.getSalesQuantity(), a.getSalesQuantity()))
	        .limit(3)
	        .collect(Collectors.toList()));
	    
	    result.put("topReturns", allStats.stream()
	        .sorted((a, b) -> Double.compare(b.getReturnRate(), a.getReturnRate()))
	        .limit(3)
	        .collect(Collectors.toList()));
	        
	    return ResponseEntity.ok(result);
	}

}
