package marketMaster.bean.askForLeave;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import marketMaster.bean.employee.EmpBean;

@Entity
@Table(name = "leave_record")
public class LeaveRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "record_id")
	private Integer recordId;

	@ManyToOne
	@JoinColumn(name = "employee_id")
	private EmpBean empBean;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private LeaveCategory leaveCategory;

	@Column(name = "year")
	private int year;

	@Column(name = "actual_hours")
	private int actualHours;

	@OneToMany(mappedBy = "leaveRecord", cascade = CascadeType.ALL)
	private List<AskForLeave> askForLeaveList;

	public LeaveRecord() {
	}

	public LeaveRecord(EmpBean empBean, LeaveCategory leaveCategory, int year, int actualHours,
			List<AskForLeave> askForLeaveList) {
		this.empBean = empBean;
		this.leaveCategory = leaveCategory;
		this.year = year;
		this.actualHours = actualHours;
		this.askForLeaveList = askForLeaveList;
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

	public LeaveCategory getLeaveCategory() {
		return leaveCategory;
	}

	public void setLeaveCategory(LeaveCategory leaveCategory) {
		this.leaveCategory = leaveCategory;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getActualHours() {
		return actualHours;
	}

	public void setActualHours(int actualHours) {
		this.actualHours = actualHours;
	}

	public List<AskForLeave> getAskForLeaveList() {
		return askForLeaveList;
	}

	public void setAskForLeaveList(List<AskForLeave> askForLeaveList) {
		this.askForLeaveList = askForLeaveList;
	}

}
