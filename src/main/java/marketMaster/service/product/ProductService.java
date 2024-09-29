package marketMaster.service.product;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import marketMaster.bean.product.ProductBean;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepo;

	public ProductBean addProduct(ProductBean product) {
		return productRepo.save(product);
	}

	public ProductBean findOneProduct(String productId) {
		Optional<ProductBean> optional = productRepo.findById(productId);

		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	public List<ProductBean> findAllProduct() {
		return productRepo.findAll();
	}

	public ProductBean shelveProduct(String productId, Integer newShelveNumber) {
		Optional<ProductBean> optional = productRepo.findById(productId);

		if (optional.isPresent()) {
			ProductBean product = optional.get();
			int inventory = product.getNumberOfInventory();
			int shelve = product.getNumberOfShelve();
			product.setNumberOfInventory(inventory - newShelveNumber);
			product.setNumberOfShelve(shelve + newShelveNumber);
			return product;
		}
		return null;
	}

	public ProductBean updateProduct(ProductBean newProduct) {

		String productId = newProduct.getProductId();
		Optional<ProductBean> optional = productRepo.findById(productId);

		if (optional.isPresent()) {
			ProductBean product = optional.get();

			product.setProductName(newProduct.getProductName());
			product.setProductCategory(newProduct.getProductCategory());
			product.setProductSafeInventory(newProduct.getProductSafeInventory());
			product.setProductPrice(newProduct.getProductPrice());
			return product;
		}
		return null;
	}
	
	public ProductBean removeProduct(String productId) {
		Optional<ProductBean> optional = productRepo.findById(productId);

		if (optional.isPresent()) {
			ProductBean product = optional.get();
			int inventory = product.getNumberOfInventory();
			int shelve = product.getNumberOfShelve();
			int remove = product.getNumberOfRemove();
			product.setNumberOfInventory(0);
			product.setNumberOfShelve(0);
			product.setNumberOfRemove(remove + shelve + inventory);
			return product;
		}
		return null;
	}
}
