package marketMaster.bean.askForLeave;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import marketMaster.bean.employee.EmpBean;

@Entity
@Table(name = "leave_record")
public class LeaveRecordBean {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "record_id")
	private Integer recordId;

	@ManyToOne
	@JoinColumn(name = "employee_id")
	private EmpBean empBean;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private LeaveCategoryBean leaveCategory;

	@Column(name = "expiration_date")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate expirationDate;

	@Column(name = "actual_hours")
	private Integer actualHours;

	@Column(name = "limit_hours")
	private Integer limitHours;
	
	

	public LeaveRecordBean() {
	}

	public LeaveRecordBean(EmpBean empBean, LeaveCategoryBean leaveCategory, LocalDate expirationDate, int actualHours,
			int limitHours) {
		super();
		this.empBean = empBean;
		this.leaveCategory = leaveCategory;
		this.expirationDate = expirationDate;
		this.actualHours = actualHours;
		this.limitHours = limitHours;
	}

	public Integer getRecordId() {
		return recordId;
	}

	public void setRecordId(Integer recordId) {
		this.recordId = recordId;
	}

	public EmpBean getEmpBean() {
		return empBean;
	}

	public void setEmpBean(EmpBean empBean) {
		this.empBean = empBean;
	}

	public LeaveCategoryBean getLeaveCategory() {
		return leaveCategory;
	}

	public void setLeaveCategory(LeaveCategoryBean leaveCategory) {
		this.leaveCategory = leaveCategory;
	}

	public LocalDate getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(LocalDate expirationDate) {
		this.expirationDate = expirationDate;
	}

	public int getActualHours() {
		return actualHours;
	}

	public void setActualHours(int actualHours) {
		this.actualHours = actualHours;
	}

	public int getLimitHours() {
		return limitHours;
	}

	public void setLimitHours(int limitHours) {
		this.limitHours = limitHours;
	}

	@Override
	public String toString() {
		return "LeaveRecordBean [recordId=" + recordId + ", empBean=" + empBean + ", leaveCategory=" + leaveCategory
				+ ", expirationDate=" + expirationDate + ", actualHours=" + actualHours + ", limitHours=" + limitHours
				+ "]";
	}

}
