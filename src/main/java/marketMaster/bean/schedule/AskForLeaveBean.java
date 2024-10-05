package marketMaster.bean.schedule;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.format.annotation.DateTimeFormat;

import marketMaster.bean.employee.EmpBean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ask_for_leave")
public class AskForLeaveBean implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "leave_id")
	private String leaveId;

	@Column(name = "employee_id")
	private String employeeId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id", insertable=false, updatable=false)
	private EmpBean empBean;

	@Column(name = "start_datetime")
	private LocalDateTime startDatetime;

	@Column(name = "end_datetime")
	private LocalDateTime endDatetime;

	@Column(name = "leave_category")
	private String leaveCategory;

	@Column(name = "reason_of_leave")
	private String reasonOfLeave;

	@Column(name = "approved_status")
	private String approvedStatus;


	public AskForLeaveBean() {
		super();
	}

	public AskForLeaveBean(String leaveId, String employeeId, LocalDateTime startDatetime, LocalDateTime endDatetime,
			String leaveCategory, String reasonOfLeave, String approvedStatus,
			marketMaster.bean.employee.EmpBean empBean) {
		super();
		this.leaveId = leaveId;
		this.employeeId = employeeId;
		this.startDatetime = startDatetime;
		this.endDatetime = endDatetime;
		this.leaveCategory = leaveCategory;
		this.reasonOfLeave = reasonOfLeave;
		this.approvedStatus = approvedStatus;
		this.empBean = empBean;
	}

	public String getLeaveId() {
		return leaveId;
	}

	public void setLeaveId(String leaveId) {
		this.leaveId = leaveId;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public LocalDateTime getStartDatetime() {
		return startDatetime;
	}

	public void setStartDatetime(LocalDateTime startDatetime) {
		this.startDatetime = startDatetime;
	}

	public LocalDateTime getEndDatetime() {
		return endDatetime;
	}

	public void setEndDatetime(LocalDateTime endDatetime) {
		this.endDatetime = endDatetime;
	}

	public String getLeaveCategory() {
		return leaveCategory;
	}

	public void setLeaveCategory(String leaveCategory) {
		this.leaveCategory = leaveCategory;
	}

	public String getReasonOfLeave() {
		return reasonOfLeave;
	}

	public void setReasonOfLeave(String reasonOfLeave) {
		this.reasonOfLeave = reasonOfLeave;
	}

	public EmpBean getEmpBean() {
		return empBean;
	}

	public void setEmpBean(EmpBean empBean) {
		this.empBean = empBean;
	}

	public String getApprovedStatus() {
		return approvedStatus;
	}

	public void setApprovedStatus(String approvedStatus) {
		this.approvedStatus = approvedStatus;
	}

	public String getStartDatetimeFormatted() {
		return startDatetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
	}

	public String getEndDatetimeFormatted() {
		return endDatetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
	}

}