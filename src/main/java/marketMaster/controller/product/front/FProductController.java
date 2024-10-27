package marketMaster.controller.product.front;

import java.io.IOException;
import java.util.List;

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
import org.springframework.web.multipart.MultipartFile;

import marketMaster.DTO.product.ProductCategoryDTO;
import marketMaster.bean.product.ProductBean;
import marketMaster.service.product.ProductService;

@Controller
public class FProductController {

	@Autowired
	private ProductService productService;

	@Autowired
	private FProductRestController productRestController;
	
	@GetMapping("/product/front/test")
	public String testPage() {
		return "/product/front/test";
	}
	
	@GetMapping("/product/front/addPage")
	public String addProductPage(Model m) {
		List<ProductCategoryDTO> productCategory = productRestController.getProductCategory();

		m.addAttribute("categorys", productCategory);
		return "/product/front/insertProduct";
	}

	@Transactional
	@PostMapping("/product/front/add")
	public String addProduct(@ModelAttribute ProductBean product, @RequestParam MultipartFile photo, Model m)
			throws IOException {

		ProductBean newProduct = productService.addProduct(product, photo);
		System.out.println(product.getProductCategory());
		if (newProduct == null) {
			m.addAttribute("errorMsg", "商品編號已存在");

			return "/product/front/insertProduct";
		}
		m.addAttribute("message", "新增商品資料成功");

		m.addAttribute("product", newProduct);
		return "/product/front/showChangePage";
	}

	@GetMapping("/product/front/downloadProductPhoto")
	public ResponseEntity<?> getProductPhoto(@RequestParam String productId) {
		ProductBean product = productService.findOneProduct(productId);

		byte[] productPhotoByte = product.getProductPhoto();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);

		return new ResponseEntity<byte[]>(productPhotoByte, headers, HttpStatus.OK);
	}

	@GetMapping("/product/front/findOne")
	public String getOneProduct(@RequestParam String productId, Model m) {
		ProductBean product = productService.findOneProduct(productId);

		byte[] productPhotoByte = product.getProductPhoto();

		System.out.println(productPhotoByte);
		m.addAttribute("product", product);
		return "product/findOnePage";
	}

	@GetMapping("/product/front/findProductAvailable") // 測試中 先預設false
	public String getProductAvailable(@RequestParam(defaultValue = "false") boolean available,
			@RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
			@RequestParam(value = "size", defaultValue = "10") Integer pageSize, Model m) {

		Page<ProductBean> products = productService.findProductAvailable(available, pageNumber, pageSize);

		m.addAttribute("products", products);
		m.addAttribute("pages", products);

		return "product/findAllPage";
	}

	@GetMapping("/product/front/findProductInventoryNotEnough")
	public String getProductInventoryNotEnough(@RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
			@RequestParam(value = "size", defaultValue = "10") Integer pageSize, Model m) {
		Page<ProductBean> products = productService.findProductNotEnough(pageNumber, pageSize);

		m.addAttribute("products", products);
		m.addAttribute("pages", products);
		return "product/findAllPage";
	}

	@GetMapping("/product/front/findAll")
	public String getAllProduct(@RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
			@RequestParam(value = "size", defaultValue = "10") Integer pageSize, Model m) {

		Page<ProductBean> products = productService.findAllProduct(pageNumber, pageSize);

		m.addAttribute("products", products);
		m.addAttribute("pages", products);

		return "product/findAllPage";
	}

	@GetMapping("/product/front/getUpdate")
	public String getUpdateProduct(@RequestParam String productId, Model m) {
		ProductBean product = productService.findOneProduct(productId);

		m.addAttribute("product", product);
		return "product/getUpdatePage";
	}

	@Transactional
	@PostMapping("/product/front/update")
	public String updateProduct(@ModelAttribute ProductBean product, @RequestParam MultipartFile photo, Model m)
			throws IOException {
		ProductBean newProduct = productService.updateProduct(product,photo);
		m.addAttribute("message", "成功更新商品資料");
		m.addAttribute("product", newProduct);
		return "/product/front/showChangePage";
	}

	@GetMapping("/product/front/getphotopage")
	public String updatephotopage() {
		return "/product/front/getupdatephoto";
	}

	@Transactional
	@PostMapping("/product/front/updatephoto")
	public void updateProductPhoto(@RequestParam String productId, @RequestParam MultipartFile photo, Model m) {
		try {
			productService.updateProductPhoto(productId, photo);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@GetMapping("/product/front/getShelve")
	public String getShelve(@RequestParam String productId, Model m) {
		ProductBean product = productService.findOneProduct(productId);

		m.addAttribute("product", product);
		return "product/getShelvePage";
	}

	@Transactional
	@PostMapping("/product/front/shelve")
	public String shelveProduct(@RequestParam String productId, @RequestParam Integer numberOfShelve, Model m) {
		ProductBean newProduct = productService.shelveProduct(productId, numberOfShelve);

		m.addAttribute("message", "成功上架商品");
		m.addAttribute("product", newProduct);
		return "/product/front/showChangePage";
	}

	@Transactional
	@PostMapping("/product/front/remove")
	public String postMethodName(@RequestParam String productId) {
		productService.removeProduct(productId);

		return "redirect:/product/front/findAll";
	}

}
