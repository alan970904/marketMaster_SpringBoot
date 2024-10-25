package marketMaster.bean.schedule;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import marketMaster.bean.employee.EmpBean;
import jakarta.persistence.Column;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "schedule")
public class ScheduleBean {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "schedule_id")
	private Integer scheduleId;

	@ManyToOne
	@JoinColumn(name = "employee_id")
	private EmpBean empBean;

	@Column(name = "schedule_date")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate scheduleDate;

	@Column(name = "start_time")
	@JsonFormat(pattern = "HH:mm")
	private LocalTime startTime;

	@Column(name = "end_time")
	@JsonFormat(pattern = "HH:mm")
	private LocalTime endTime;

	@Column(name = "schedule_hour")
	private Integer scheduleHour;

	@Column(name = "schedule_active")
	private Boolean scheduleActive = true;

	public ScheduleBean() {
	}

	public ScheduleBean(EmpBean empBean, LocalDate scheduleDate, LocalTime startTime, LocalTime endTime,
			Integer scheduleHour, Boolean scheduleActive) {
		this.empBean = empBean;
		this.scheduleDate = scheduleDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.scheduleHour = scheduleHour;
		this.scheduleActive = scheduleActive;
	}

	public Integer getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Integer scheduleId) {
		this.scheduleId = scheduleId;
	}

	public EmpBean getEmpBean() {
		return empBean;
	}

	public void setEmpBean(EmpBean empBean) {
		this.empBean = empBean;
	}

	public LocalDate getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(LocalDate scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public Integer getScheduleHour() {
		return scheduleHour;
	}

	public void setScheduleHour(Integer scheduleHour) {
		this.scheduleHour = scheduleHour;
	}

	public Boolean getScheduleActive() {
		return scheduleActive;
	}

	public void setScheduleActive(Boolean scheduleActive) {
		this.scheduleActive = scheduleActive;
	}

	@Override
	public String toString() {
		return "ScheduleBean [scheduleId=" + scheduleId + ", empBean=" + empBean + ", scheduleDate=" + scheduleDate
				+ ", startTime=" + startTime + ", endTime=" + endTime + ", scheduleHour=" + scheduleHour
				+ ", scheduleActive=" + scheduleActive + "]";
	}
	

}
