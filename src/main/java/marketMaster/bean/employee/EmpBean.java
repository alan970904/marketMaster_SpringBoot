package marketMaster.bean.employee;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "employee")
public class EmpBean implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "employee_id")
    private String employeeId;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "employee_tel")
    private String employeeTel;

    @Column(name = "employee_idcard")
    private String employeeIdcard;

    @Column(name = "employee_email")
    private String employeeEmail;

    @Column(name = "password")
    private String password;

    @Column(name = "position_id")
    private String positionId;

    @Column(name = "hiredate")
    private LocalDate hiredate;

    @Column(name = "resigndate")
    private LocalDate resigndate;

    @Column(name = "is_first_login")
    private boolean isFirstLogin;
    
    @Column(name = "image_path")
    private String imagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", referencedColumnName = "position_id", insertable = false, updatable = false)
    private RankLevelBean rankLevel;
    
    @JsonIgnore
    @OneToMany(mappedBy = "fromEmployee", fetch = FetchType.LAZY)
    private List<ChatMessage> sentMessages;

    @JsonIgnore
    @OneToMany(mappedBy = "toEmployee", fetch = FetchType.LAZY)
    private List<ChatMessage> receivedMessages;
    
    //獲取員工的權限等級
    public int getAuthority() {
		return this.rankLevel.getLimitsOfAuthority();
	}
    
    public EmpBean() {
    }

	public EmpBean(String employeeId, String employeeName, String employeeTel, String employeeIdcard,
			String employeeEmail, String password, String positionId, LocalDate hiredate, LocalDate resigndate,
			boolean isFirstLogin, String imagePath, RankLevelBean rankLevel) {
		super();
		this.employeeId = employeeId;
		this.employeeName = employeeName;
		this.employeeTel = employeeTel;
		this.employeeIdcard = employeeIdcard;
		this.employeeEmail = employeeEmail;
		this.password = password;
		this.positionId = positionId;
		this.hiredate = hiredate;
		this.resigndate = resigndate;
		this.isFirstLogin = isFirstLogin;
		this.imagePath = imagePath;
		this.rankLevel = rankLevel;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getEmployeeTel() {
		return employeeTel;
	}

	public void setEmployeeTel(String employeeTel) {
		this.employeeTel = employeeTel;
	}

	public String getEmployeeIdcard() {
		return employeeIdcard;
	}

	public void setEmployeeIdcard(String employeeIdcard) {
		this.employeeIdcard = employeeIdcard;
	}

	public String getEmployeeEmail() {
		return employeeEmail;
	}

	public void setEmployeeEmail(String employeeEmail) {
		this.employeeEmail = employeeEmail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPositionId() {
		return positionId;
	}

	public void setPositionId(String positionId) {
		this.positionId = positionId;
	}

	public LocalDate getHiredate() {
		return hiredate;
	}

	public void setHiredate(LocalDate hiredate) {
		this.hiredate = hiredate;
	}

	public LocalDate getResigndate() {
		return resigndate;
	}

	public void setResigndate(LocalDate resigndate) {
		this.resigndate = resigndate;
	}

	public boolean isFirstLogin() {
		return isFirstLogin;
	}

	public void setFirstLogin(boolean isFirstLogin) {
		this.isFirstLogin = isFirstLogin;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public RankLevelBean getRankLevel() {
		return rankLevel;
	}

	public void setRankLevel(RankLevelBean rankLevel) {
		this.rankLevel = rankLevel;
	}

	public List<ChatMessage> getSentMessages() {
		return sentMessages;
	}

	public void setSentMessages(List<ChatMessage> sentMessages) {
		this.sentMessages = sentMessages;
	}

	public List<ChatMessage> getReceivedMessages() {
		return receivedMessages;
	}

	public void setReceivedMessages(List<ChatMessage> receivedMessages) {
		this.receivedMessages = receivedMessages;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
