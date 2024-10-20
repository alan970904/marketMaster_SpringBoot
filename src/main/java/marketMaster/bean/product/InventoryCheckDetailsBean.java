package marketMaster.bean.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory_check_details")
public class InventoryCheckDetailsBean {

	@Id
	@Column(name = "detail_id")
	private String detailId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inventory_check_id")
	@JsonBackReference
	private InventoryCheckBean inventoryCheck;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private ProductBean product;

	@Column(name = "current_inventory")
	private Integer currentInventory;

	@Column(name = "actual_inventory")
	private Integer actualInventory;

	@Column(name = "differential_inventory")
	private Integer differentialInventory;

	@Column(name = "remark")
	private String remark;

	public InventoryCheckDetailsBean(String detailId, InventoryCheckBean inventoryCheck, ProductBean product,
			Integer currentInventory, Integer actualInventory, Integer differentialInventory, String remark) {
		super();
		this.detailId = detailId;
		this.inventoryCheck = inventoryCheck;
		this.product = product;
		this.currentInventory = currentInventory;
		this.actualInventory = actualInventory;
		this.differentialInventory = differentialInventory;
		this.remark = remark;
	}
	
	

	public InventoryCheckDetailsBean(String detailId, Integer currentInventory, Integer actualInventory,
			Integer differentialInventory, String remark) {
		super();
		this.detailId = detailId;
		this.currentInventory = currentInventory;
		this.actualInventory = actualInventory;
		this.differentialInventory = differentialInventory;
		this.remark = remark;
	}



	public InventoryCheckDetailsBean() {
		super();
	}



	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public InventoryCheckBean getInventoryCheck() {
		return inventoryCheck;
	}

	public void setInventoryCheck(InventoryCheckBean inventoryCheck) {
		this.inventoryCheck = inventoryCheck;
	}

	public ProductBean getProduct() {
		return product;
	}

	public void setProduct(ProductBean product) {
		this.product = product;
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

	public Integer getDifferentialInventory() {
		return differentialInventory;
	}

	public void setDifferentialInventory(Integer differentialInventory) {
		this.differentialInventory = differentialInventory;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
