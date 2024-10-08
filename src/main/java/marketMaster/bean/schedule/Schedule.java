package marketMaster.bean.schedule;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import marketMaster.bean.employee.EmpBean;

@Entity
@Table(name = "schedule")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Integer scheduleId;  

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")  
    private EmpBean empBean;

    @Column(name = "start_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endTime;


	public Schedule() {
	}


	public Schedule(EmpBean empBean, LocalDateTime startTime, LocalDateTime endTime) {
		this.empBean = empBean;
		this.startTime = startTime;
		this.endTime = endTime;
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


	public LocalDateTime getStartTime() {
		return startTime;
	}


	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}


	public LocalDateTime getEndTime() {
		return endTime;
	}


	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	

	

}