package marketMaster.bean.product;

import jakarta.persistence.*;
import marketMaster.bean.restock.SupplierProductsBean;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "products")
public class ProductBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "product_id")
	private String productId;

	@Column(name = "product_category")
	private String productCategory;

	@Column(name = "product_name")
	private String productName;

	@Column(name = "product_price")
	private int productPrice;

	@Column(name = "product_safeinventory")
	private int productSafeInventory;

	@Column(name = "number_of_shelve")
	private int numberOfShelve;

	@Column(name = "number_of_inventory")
	private int numberOfInventory;

	@Column(name = "number_of_sale")
	private int numberOfSale;

	@Column(name = "number_of_exchange")
	private int numberOfExchange;

	@Column(name = "number_of_destruction")
	private int numberOfDestruction;

	@Column(name = "number_of_remove")
	private int numberOfRemove;

	@Column(name = "product_available")
	private boolean productAvailable;

	@Column(name = "is_perishable")
	private boolean isPerishable;

	@Lob
	@Column(name = "product_photo")
	private byte[] productPhoto;

	@OneToMany(mappedBy = "product",cascade = CascadeType.ALL)
	@JsonIgnore
	private List<SupplierProductsBean>  supplierProductBean;
	
	public ProductBean() {
		super();
	}

	public ProductBean(String productId, String productCategory, String productName, int productPrice,
					   int productSafeInventory, int numberOfShelve, int numberOfInventory, int numberOfSale, int numberOfExchange,
					   int numberOfDestruction, int numberOfRemove, boolean productAvailable, boolean isPerishable,
					   byte[] productPhoto) {
		super();
		this.productId = productId;
		this.productCategory = productCategory;
		this.productName = productName;
		this.productPrice = productPrice;
		this.productSafeInventory = productSafeInventory;
		this.numberOfShelve = numberOfShelve;
		this.numberOfInventory = numberOfInventory;
		this.numberOfSale = numberOfSale;
		this.numberOfExchange = numberOfExchange;
		this.numberOfDestruction = numberOfDestruction;
		this.numberOfRemove = numberOfRemove;
		this.productAvailable = productAvailable;
		this.isPerishable = isPerishable;
		this.productPhoto = productPhoto;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(int productPrice) {
		this.productPrice = productPrice;
	}

	public int getProductSafeInventory() {
		return productSafeInventory;
	}

	public void setProductSafeInventory(int productSafeInventory) {
		this.productSafeInventory = productSafeInventory;
	}

	public int getNumberOfShelve() {
		return numberOfShelve;
	}

	public void setNumberOfShelve(int numberOfShelve) {
		this.numberOfShelve = numberOfShelve;
	}

	public int getNumberOfInventory() {
		return numberOfInventory;
	}

	public void setNumberOfInventory(int numberOfInventory) {
		this.numberOfInventory = numberOfInventory;
	}

	public int getNumberOfSale() {
		return numberOfSale;
	}

	public void setNumberOfSale(int numberOfSale) {
		this.numberOfSale = numberOfSale;
	}

	public int getNumberOfExchange() {
		return numberOfExchange;
	}

	public void setNumberOfExchange(int numberOfExchange) {
		this.numberOfExchange = numberOfExchange;
	}

	public int getNumberOfDestruction() {
		return numberOfDestruction;
	}

	public void setNumberOfDestruction(int numberOfDestruction) {
		this.numberOfDestruction = numberOfDestruction;
	}

	public int getNumberOfRemove() {
		return numberOfRemove;
	}

	public void setNumberOfRemove(int numberOfRemove) {
		this.numberOfRemove = numberOfRemove;
	}

	public boolean isProductAvailable() {
		return productAvailable;
	}

	public void setProductAvailable(boolean productAvailable) {
		this.productAvailable = productAvailable;
	}

	public boolean isPerishable() {
		return isPerishable;
	}

	public void setPerishable(boolean isPerishable) {
		this.isPerishable = isPerishable;
	}

	public byte[] getProductPhoto() {
		return productPhoto;
	}

	public void setProductPhoto(byte[] productPhoto) {
		this.productPhoto = productPhoto;
	}
	
	

	public List<SupplierProductsBean> getSupplierProductBean() {
		return supplierProductBean;
	}

	public void setSupplierProductBean(List<SupplierProductsBean> supplierProductBean) {
		this.supplierProductBean = supplierProductBean;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}




}