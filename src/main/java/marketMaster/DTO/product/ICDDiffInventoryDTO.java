package marketMaster.DTO.product;

public class ICDDiffInventoryDTO {
	private Integer differentialInventory;

	public ICDDiffInventoryDTO(Integer differentialInventory) {
		super();
		this.differentialInventory = differentialInventory;
	}

	public ICDDiffInventoryDTO() {
		super();
	}

	public Integer getDifferentialInventory() {
		return differentialInventory;
	}

	public void setDifferentialInventory(Integer differentialInventory) {
		this.differentialInventory = differentialInventory;
	}
	
}
