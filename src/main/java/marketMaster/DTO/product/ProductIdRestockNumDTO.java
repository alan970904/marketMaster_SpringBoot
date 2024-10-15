package marketMaster.DTO.product;

public class ProductIdRestockNumDTO {

	private String productId;
	private Integer numberOfRestock;
	
	public ProductIdRestockNumDTO(String productId, Integer numberOfRestock) {
		super();
		this.productId = productId;
		this.numberOfRestock = numberOfRestock;
	}

	public ProductIdRestockNumDTO() {
		super();
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public Integer getNumberOfRestock() {
		return numberOfRestock;
	}

	public void setNumberOfRestock(Integer numberOfRestock) {
		this.numberOfRestock = numberOfRestock;
	}
	
}
