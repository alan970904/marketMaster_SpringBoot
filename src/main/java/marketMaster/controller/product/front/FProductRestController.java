package marketMaster.controller.product.front;

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
import marketMaster.bean.restock.SupplierProductsBean;
import marketMaster.service.product.ProductService;

@RestController
public class FProductRestController {

	@Autowired
	private ProductService productService;

	@PostMapping("/product/front/findProductByLike/json")
	public Page<ProductBean> getProductsByLike(@RequestBody ProductPageDTO productPageDTO){
		String productName = productPageDTO.getProductName();
		Integer pageNumber = productPageDTO.getPageNumber();
		Page<ProductBean> products = productService.findProductByLike(productName,pageNumber);
		
		return products;
	}
	
	@PostMapping("/product/front/findRestockDetails/json")
	public void getRestockDetails(@RequestParam String productId){
		ProductBean aProduct = productService.findOneProduct(productId);
		List<SupplierProductsBean> supplierProduct = aProduct.getSupplierProductBean();
		for (SupplierProductsBean supplierProductsBean : supplierProduct) {
			String supplierProductId = supplierProductsBean.getSupplierProductId();
			List<ProductSupplierDTO> productSupplierDTO = productService.findRestockDetails(supplierProductId);
			for (ProductSupplierDTO aProductSupplierDTO : productSupplierDTO) {
				System.out.println(aProductSupplierDTO.getProductId());
				System.out.println(aProductSupplierDTO.getDueDate());
			}
		}
	}
	
	@PostMapping("/product/front/findProductInventoryNotEnough/json")
	public Page<ProductBean>  getProductInventoryNotEnough(@RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
			@RequestParam(value = "size", defaultValue = "10") Integer pageSize) {
		Page<ProductBean> products = productService.findProductNotEnough(pageNumber, pageSize);

		return products;
	}
	
	@PostMapping("/product/front/findProductCategory")
	public List<ProductCategoryDTO> getProductCategory() {
		List<ProductCategoryDTO> productCategory = productService.findProductCategory();
		return productCategory;
	}


}
