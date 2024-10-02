package marketMaster.bean.schedule;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

import marketMaster.bean.employee.EmpBean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "schedule")
public class ScheduleBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "employee_id")
	private String employeeId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id", insertable = false, updatable = false)
	private EmpBean empBean;

	@Column(name = "job_date")
	private Date jobDate;

	@Column(name = "start_time")
	private Time startTime;

	@Column(name = "end_time")
	private Time endTime;

	public ScheduleBean() {
		super();
	}

	public ScheduleBean(String employeeId, EmpBean empBean, Date jobDate, Time startTime, Time endTime) {
		super();
		this.employeeId = employeeId;
		this.empBean = empBean;
		this.jobDate = jobDate;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public EmpBean getEmpBean() {
		return empBean;
	}

	public void setEmpBean(EmpBean empBean) {
		this.empBean = empBean;
	}

	public Date getJobDate() {
		return jobDate;
	}

	public void setJobDate(Date jobDate) {
		this.jobDate = jobDate;
	}

	public Time getStartTime() {
		return startTime;
	}

	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}

	public Time getEndTime() {
		return endTime;
	}

	public void setEndTime(Time endTime) {
		this.endTime = endTime;
	}

	@Override
	public String toString() {
		return "ScheduleBean [employeeId=" + employeeId + ", jobDate=" + jobDate + ", startTime=" + startTime
				+ ", endTime=" + endTime + "]";
	}

}
