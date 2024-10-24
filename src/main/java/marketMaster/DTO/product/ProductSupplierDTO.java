package marketMaster.DTO.product;

import java.time.LocalDate;

public class ProductSupplierDTO {

	
	private String productId;
	
	private LocalDate dueDate;

	public ProductSupplierDTO(String productId, LocalDate dueDate) {
		super();
		this.productId = productId;
		this.dueDate = dueDate;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public ProductSupplierDTO() {
		super();
	}
	
	
}
