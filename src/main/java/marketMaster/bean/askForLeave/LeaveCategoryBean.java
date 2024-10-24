package marketMaster.bean.askForLeave;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "leave_category")
public class LeaveCategoryBean {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_id")
	private Integer categoryId;

	@Column(name = "leave_type")
	private String leaveType;

	@Column(name = "max_hours")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer maxHours;

	public LeaveCategoryBean() {
	}

	public LeaveCategoryBean(String leaveType, int maxHours) {
		super();
		this.leaveType = leaveType;
		this.maxHours = maxHours;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public void setMaxHours(Integer maxHours) {
		this.maxHours = maxHours;
	}

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public int getMaxHours() {
		return maxHours;
	}

	public void setMaxHours(int maxHours) {
		this.maxHours = maxHours;
	}

}
