package marketMaster.DTO.product;

public class ProductPageDTO {

	private String productName;

	private int pageNumber;

	public ProductPageDTO(String productName, int pageNumber) {
		this.productName = productName;
		this.pageNumber = pageNumber;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

}