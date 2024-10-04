package marketMaster.DTO.product;

public class ProductDTO {
	private String productId;

	private String productCategory;

	private String productName;

	private int productPrice;

	private int productSafeInventory;

	private int numberOfShelve;

	private int numberOfInventory;

	private int numberOfSale;

	private int numberOfExchange;

	private int numberOfDestruction;

	private int numberOfRemove;
	
	public ProductDTO(String productId, String productCategory, String productName, int productPrice,
			int productSafeInventory, int numberOfShelve, int numberOfInventory, int numberOfSale, int numberOfExchange,
			int numberOfDestruction, int numberOfRemove) {
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
	}

	public ProductDTO(String productId, int numberOfShelve, int numberOfInventory) {
		super();
		this.productId = productId;
		this.numberOfShelve = numberOfShelve;
		this.numberOfInventory = numberOfInventory;
	}

	public ProductDTO(String productId, String productName, String productCategory, int productPrice) {
		this.productId = productId;
		this.productName = productName;
		this.productCategory = productCategory;
		this.productPrice = productPrice;
	}

	public ProductDTO(String productName) {
		super();
		this.productName = productName;
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
	
	
}
