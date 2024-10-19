package marketMaster.DTO.product;

import java.time.LocalDate;
import java.util.List;

import marketMaster.bean.product.InventoryCheckDetailsBean;

public class InventoryCheckInsertDTO {
	
	private String inventoryCheckId;
	
	private String employeeId;
	
	private LocalDate inventoryCheckDate;
	
	private List<InventoryCheckDetailDTO> details;

	public InventoryCheckInsertDTO(String inventoryCheckId, String employeeId, LocalDate inventoryCheckDate,
			List<InventoryCheckDetailDTO> details) {
		super();
		this.inventoryCheckId = inventoryCheckId;
		this.employeeId = employeeId;
		this.inventoryCheckDate = inventoryCheckDate;
		this.details = details;
	}

	public InventoryCheckInsertDTO() {
		super();
	}

	public String getInventoryCheckId() {
		return inventoryCheckId;
	}

	public void setInventoryCheckId(String inventoryCheckId) {
		this.inventoryCheckId = inventoryCheckId;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public LocalDate getInventoryCheckDate() {
		return inventoryCheckDate;
	}

	public void setInventoryCheckDate(LocalDate inventoryCheckDate) {
		this.inventoryCheckDate = inventoryCheckDate;
	}

	public List<InventoryCheckDetailDTO> getDetails() {
		return details;
	}

	public void setDetails(List<InventoryCheckDetailDTO> details) {
		this.details = details;
	}


	
	
	
}
