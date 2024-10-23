package marketMaster.DTO.product;

public class ProductPageDTO {

	private String productName;

	private String productCategory;
	
	private Integer pageNumber;
	
	private Integer pageSize;

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public ProductPageDTO(String productName, String productCategory, Integer pageNumber, Integer pageSize) {
		super();
		this.productName = productName;
		this.productCategory = productCategory;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
	}

	public ProductPageDTO() {
		super();
	}




	

}