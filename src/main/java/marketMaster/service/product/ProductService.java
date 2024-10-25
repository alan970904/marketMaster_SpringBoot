package marketMaster.service.product;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import marketMaster.DTO.product.ProductCategoryDTO;
import marketMaster.DTO.product.ProductIdRestockNumDTO;
import marketMaster.DTO.product.ProductSupplierDTO;
import marketMaster.bean.product.ProductBean;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepo;

	// ===================== 新增商品 =====================
	public ProductBean addProduct(ProductBean product, MultipartFile photo) throws IOException {
		Optional<ProductBean> exist = productRepo.findById(product.getProductId());

		if (!exist.isPresent()) {
			product.setNumberOfShelve(0);
			product.setNumberOfInventory(0);
			product.setNumberOfSale(0);
			product.setNumberOfExchange(0);
			product.setNumberOfDestruction(0);
			product.setNumberOfRemove(0);
			product.setProductAvailable(true);
			product.setPerishable(false);
			if (photo != null) {
				product.setProductPhoto(photo.getBytes());
			}
			return productRepo.save(product);
		} else {
			return null;
		}

	}

// ===================== 查詢商品 ========================
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

	public Page<ProductBean> findProductByLike(String productName, Integer pageNumber) {
		Pageable pgb = PageRequest.of(pageNumber - 1, 10);
		Page<ProductBean> products = productRepo.findByProductNameContaining(productName, pgb);

		return products;
	}

	public Page<ProductBean> findProductByCategory(String productCategory, Integer pageNumber, Integer pageSize) {
		Pageable pgb = PageRequest.of(pageNumber - 1, pageSize);
		Page<ProductBean> products = productRepo.findByProductCategory(productCategory, pgb);
		return products;
	}
	public Page<ProductBean> findProductByCategoryAndAvilable(String productCategory,boolean isAvailable, Integer pageNumber, Integer pageSize) {
		Pageable pgb = PageRequest.of(pageNumber - 1, pageSize);
		Page<ProductBean> products = productRepo.findByProductAvailableAndProductCategory(isAvailable,productCategory, pgb);
		return products;
	}

	public Page<ProductBean> findProductAvailable(boolean isAvailable, Integer pageNumber, Integer pageSize) {
		Pageable pgb = PageRequest.of(pageNumber - 1, 10);
		Page<ProductBean> products = productRepo.findByProductAvailable(isAvailable, pgb);
		return products;
	}

	public Page<ProductBean> findProductNotEnough(Integer pageNumber, Integer pageSize) {
		Pageable pgb = PageRequest.of(pageNumber - 1, 10);
		Page<ProductBean> products = productRepo.findInventoryNotEnough(pgb);
		return products;
	}

	public List<ProductCategoryDTO> findProductCategory() {
		return productRepo.findAllCategories();

	}

//	public List<LocalDate> test(String productId) {
//		return productRepo.findProductionDatesByProductId(productId);
//	}

	public List<ProductSupplierDTO> findRestockDetails(String supplierId) {

		List<ProductSupplierDTO> restockDetails = productRepo.findBySupplierId(supplierId);

		return restockDetails;
	}

// =================== 更新商品 ==================
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

	public ProductBean updateProduct(ProductBean newProduct, MultipartFile photo) throws IOException {

		String productId = newProduct.getProductId();
		Optional<ProductBean> optional = productRepo.findById(productId);

		if (optional.isPresent()) {
			ProductBean product = optional.get();

			product.setProductName(newProduct.getProductName());
			product.setProductCategory(newProduct.getProductCategory());
			product.setProductSafeInventory(newProduct.getProductSafeInventory());
			product.setProductPrice(newProduct.getProductPrice());
			if (photo != null) {
				product.setProductPhoto(photo.getBytes());
			} else {
				product.setProductPhoto(product.getProductPhoto());
			}
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
			product.setProductAvailable(false);
			return product;
		}
		return null;
	}

	@Transactional
	public void updateProductPhoto(String productId, MultipartFile photo) throws IOException {
		Optional<ProductBean> optional = productRepo.findById(productId);

		if (optional.isPresent()) {
			ProductBean product = optional.get();

			product.setProductPhoto(photo.getBytes());
		}
	}

// ==============  進貨數量更新 ==============	
	@Transactional
	public void updateProductByInsertRestock(String productId, Integer numberOfRestock) {
		Optional<ProductBean> optional = productRepo.findById(productId);

		if (optional.isPresent()) {

			ProductBean product = optional.get();

			product.setNumberOfInventory(product.getNumberOfInventory() + numberOfRestock);
		}

	}

	@Transactional
	public void updateProductByUpdateRestock(String productId, Integer numberOfRestock, Integer oldNumberOfRestock) {

		Integer difference = numberOfRestock - oldNumberOfRestock;
		if (difference != 0) {
			Optional<ProductBean> optional = productRepo.findById(productId);

			if (optional.isPresent()) {

				ProductBean product = optional.get();

				product.setNumberOfInventory(product.getNumberOfInventory() + difference);
			}
		}

	}

	@Transactional
	public void updateProductByDeleteRestock(String productId, Integer oldNumberOfRestock) {
		Optional<ProductBean> optional = productRepo.findById(productId);
		if (optional.isPresent()) {
			ProductBean product = optional.get();
			product.setNumberOfInventory(product.getNumberOfInventory() - oldNumberOfRestock);
		}
	}

	public ProductIdRestockNumDTO findProductIdByRestockDetailId(String restockDetailId) {
		return productRepo.findProductIdByRestockDetailId(restockDetailId);
	}

//	public List<ProductIdRestockNumDTO> findRestockNumByRestockId(String restockId) {
//		return productRepo.findRestockNumberByRestockId(restockId);
//	}

//	public ProductIdRestockNumDTO findProductIdByRestockDetailId(String detailId) {
//		return productRepo.findProductIdByRestockDetailId(detailId);
//	}

	// 盤點數量更新

	@Transactional
	public void updateProductByInsertCheck(String productId, Integer actualInventory) {

		Optional<ProductBean> optional = productRepo.findById(productId);

		if (optional.isPresent()) {
			ProductBean product = optional.get();
			product.setNumberOfInventory(actualInventory);
		}
	}

	@Transactional
	public void updateProductByUpdateCheck(String productId, Integer actualInventory) {
		Optional<ProductBean> optional = productRepo.findById(productId);

		if (optional.isPresent()) {
			ProductBean product = optional.get();
			product.setNumberOfInventory(actualInventory);
		}
	}

	@Transactional
	public void updateProductByDeleteCheck(String productId, Integer currentInventory) {
		Optional<ProductBean> optional = productRepo.findById(productId);

		if (optional.isPresent()) {
			ProductBean product = optional.get();
			product.setNumberOfInventory(currentInventory);
		}
	}
	
	//結帳數量更新
	
	@Transactional 
	public void updateProductByInsertCheckOut(String productId,int numberOfCheckout) {
		Optional<ProductBean> optional = productRepo.findById(productId);
		if (optional.isPresent()) {
			ProductBean product = optional.get();
			product.setNumberOfInventory(product.getNumberOfInventory() - numberOfCheckout);
			product.setNumberOfSale(product.getNumberOfSale() + numberOfCheckout);
		}
	}
	@Transactional 
	public void updateProductByDeleteCheckOut(String productId,int numberOfCheckout) {
		Optional<ProductBean> optional = productRepo.findById(productId);
		if (optional.isPresent()) {
			ProductBean product = optional.get();
			product.setNumberOfInventory(product.getNumberOfInventory() + numberOfCheckout);
			product.setNumberOfSale(product.getNumberOfSale() - numberOfCheckout);
		}
	}
	
	//退貨數量更新
	@Transactional
	public void updateProductByInsertReturn(String productId,int numberOfReturn) {
		Optional<ProductBean> optional = productRepo.findById(productId);
		if (optional.isPresent()) {
			ProductBean product = optional.get();
			product.setNumberOfSale(product.getNumberOfSale() - numberOfReturn);
			product.setNumberOfDestruction(product.getNumberOfDestruction() + numberOfReturn);
		}
	}
}
