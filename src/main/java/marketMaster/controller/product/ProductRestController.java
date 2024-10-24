package marketMaster.controller.product;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import marketMaster.DTO.product.ProductCategoryDTO;
import marketMaster.DTO.product.ProductPageDTO;
import marketMaster.DTO.product.ProductSupplierDTO;
import marketMaster.bean.product.ProductBean;
import marketMaster.bean.restock.RestockDetailsBean;
import marketMaster.bean.restock.SupplierProductsBean;
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
	
	@PostMapping("/product/findRestockDetails/json")
	public void getRestockDetails(@RequestParam String productId){
		ProductBean aProduct = productService.findOneProduct(productId);
		List<SupplierProductsBean> supplierProduct = aProduct.getSupplierProductBean();
		for (SupplierProductsBean supplierProductsBean : supplierProduct) {
			System.out.println(supplierProductsBean);
			String supplierProductId = supplierProductsBean.getSupplierProductId();
			System.out.println(supplierProductId);
			System.out.println("==============");
			List<ProductSupplierDTO> productSupplierDTO = productService.findRestockDetails(supplierProductId);
			for (ProductSupplierDTO aProductSupplierDTO : productSupplierDTO) {
				System.out.println(aProductSupplierDTO.getProductId());
				System.out.println(aProductSupplierDTO.getDueDate());
			}
		}
	}
	
	
	@PostMapping("/product/findProductCategory")
	public List<ProductCategoryDTO> getProductCategory() {
		List<ProductCategoryDTO> productCategory = productService.findProductCategory();
		return productCategory;
	}


}
