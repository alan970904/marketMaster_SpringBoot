package marketMaster.bean.askForLeave;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import marketMaster.bean.employee.EmpBean;


@Entity
@Table(name="ask_for_leave")
public class AskForLeave {
	
	@Id
	@Column(name = "leave_id")
	private String leaveId;
	
	@ManyToOne
	@JoinColumn(name = "employee_id")
	private EmpBean empBean;
	
	@Column(name = "start_time")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime starTime;
	
	@Column(name = "end_time")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime endTime;
	
	@Column(name = "leave_type")
	private String leaveType;
	
	@Column(name = "reason_leave")
	private String reasonLeave;
	
	@Lob
	@Column(name = "proof_image")
	private byte[] proofImage;
	
	@Column(name = "approved_status")
	private String approvedStatus;

	public AskForLeave() {
	}

	
	
	public AskForLeave(String leaveId, EmpBean empBean, LocalDateTime starTime, LocalDateTime endTime, String leaveType,
			String reasonLeave, byte[] proofImage, String approvedStatus) {
		this.leaveId = leaveId;
		this.empBean = empBean;
		this.starTime = starTime;
		this.endTime = endTime;
		this.leaveType = leaveType;
		this.reasonLeave = reasonLeave;
		this.proofImage = proofImage;
		this.approvedStatus = approvedStatus;
	}



	public String getLeaveId() {
		return leaveId;
	}

	public void setLeaveId(String leaveId) {
		this.leaveId = leaveId;
	}

	public EmpBean getEmpBean() {
		return empBean;
	}

	public void setEmpBean(EmpBean empBean) {
		this.empBean = empBean;
	}

	public LocalDateTime getStarTime() {
		return starTime;
	}

	public void setStarTime(LocalDateTime starTime) {
		this.starTime = starTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public String getReasonLeave() {
		return reasonLeave;
	}

	public void setReasonLeave(String reasonLeave) {
		this.reasonLeave = reasonLeave;
	}

	public byte[] getProofImage() {
		return proofImage;
	}

	public void setProofImage(byte[] proofImage) {
		this.proofImage = proofImage;
	}

	public String getApprovedStatus() {
		return approvedStatus;
	}

	public void setApprovedStatus(String approvedStatus) {
		this.approvedStatus = approvedStatus;
	}

	
	

}