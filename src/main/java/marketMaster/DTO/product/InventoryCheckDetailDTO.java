package marketMaster.DTO.product;


public class InventoryCheckDetailDTO {
	
	private String productId;
	
	private String detailId;
	
	private Integer currentInventory;
	
	private Integer actualInventory;
	
	private String remark;

	public InventoryCheckDetailDTO() {
		super();
	}

	public InventoryCheckDetailDTO(String productId, Integer currentInventory, Integer actualInventory, String remark) {
		super();
		this.productId = productId;
		this.currentInventory = currentInventory;
		this.actualInventory = actualInventory;
		this.remark = remark;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public Integer getCurrentInventory() {
		return currentInventory;
	}

	public void setCurrentInventory(Integer currentInventory) {
		this.currentInventory = currentInventory;
	}

	public Integer getActualInventory() {
		return actualInventory;
	}

	public void setActualInventory(Integer actualInventory) {
		this.actualInventory = actualInventory;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	
	
}
