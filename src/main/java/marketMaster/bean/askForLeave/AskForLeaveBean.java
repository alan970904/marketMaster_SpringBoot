package marketMaster.bean.askForLeave;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import marketMaster.bean.employee.EmpBean;

@Entity
@Table(name = "ask_for_leave")
public class AskForLeaveBean {

	@Id
	@Column(name = "leave_id")
	private String leaveId;

	@ManyToOne
	@JoinColumn(name = "employee_id")
	private EmpBean empBean;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private LeaveCategoryBean leaveCategory;

	@Column(name = "start_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime starTime;

	@Column(name = "end_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime endTime;

	@Column(name = "reason_leave")
	private String reasonLeave;

	@Lob
	@Column(name = "proof_image")
	private byte[] proofImage;

	@Column(name = "approved_status")
	private String approvedStatus;

	@Column(name = "leave_hours")
	private Integer leaveHours;

	@Column(name = "rejection_reason")
	private String rejectionReason;


	public AskForLeaveBean() {
	}

	public AskForLeaveBean(String leaveId, EmpBean empBean, LeaveCategoryBean leaveCategory, LocalDateTime starTime,
			LocalDateTime endTime, String reasonLeave, byte[] proofImage, String approvedStatus, Integer leaveHours,
			String rejectionReason) {
		this.leaveId = leaveId;
		this.empBean = empBean;
		this.leaveCategory = leaveCategory;
		this.starTime = starTime;
		this.endTime = endTime;
		this.reasonLeave = reasonLeave;
		this.proofImage = proofImage;
		this.approvedStatus = approvedStatus;
		this.leaveHours = leaveHours;
		this.rejectionReason = rejectionReason;
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

	public LeaveCategoryBean getLeaveCategory() {
		return leaveCategory;
	}

	public void setLeaveCategory(LeaveCategoryBean leaveCategory) {
		this.leaveCategory = leaveCategory;
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

	public Integer getLeaveHours() {
		return leaveHours;
	}

	public void setLeaveHours(Integer leaveHours) {
		this.leaveHours = leaveHours;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}


}
