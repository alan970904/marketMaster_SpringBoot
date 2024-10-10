package marketMaster.service.product;

import java.beans.Transient;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import marketMaster.DTO.product.ProductCategoryDTO;
import marketMaster.bean.product.ProductBean;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepo;

	public ProductBean addProduct(ProductBean product) {
		Optional<ProductBean> exist = productRepo.findById(product.getProductId());

		if (!exist.isPresent()) {
			product.setNumberOfShelve(0);
			product.setNumberOfInventory(0);
			product.setNumberOfSale(0);
			product.setNumberOfExchange(0);
			product.setNumberOfDestruction(0);
			product.setNumberOfRemove(0);
			return productRepo.save(product);
		} else {
			return null;
		}

	}

	public ProductBean findOneProduct(String productId) {
		Optional<ProductBean> optional = productRepo.findById(productId);

		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	public Page<ProductBean> findAllProduct(Integer pageNumber, Integer pageSize) {
		Pageable pgb = PageRequest.of(pageNumber - 1, pageSize);
		Page<ProductBean> page = productRepo.findAll(pgb);
		return page;
	}
	
	public Page<ProductBean> findProductByLike(String productName,Integer pageNumber) {
		Pageable pgb = PageRequest.of(pageNumber - 1, 10);
//		String productNameQuery = "%"+productName+"%";
		Page<ProductBean> products = productRepo.findByProductNameContaining(productName,pgb);
		
		return products;
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
	
	public List<ProductCategoryDTO> findProductCategory() {
		return productRepo.findAllCategories();
		
	}
	

	@Transactional
	public void updateRestockProduct(String productId, Integer numberOfRestock) {
		Optional<ProductBean> optional = productRepo.findById(productId);
		
		if (optional.isPresent()) {
			
			ProductBean product = optional.get();
			
			product.setNumberOfInventory(product.getNumberOfInventory()+numberOfRestock);
//			productRepo.save(product);
		}
		
	}
}
