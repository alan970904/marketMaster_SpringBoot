package marketMaster.bean.employee;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "from_user")
	private String fromUser;
	
	@Column(name = "to_user")
	private String toUser;
	
	@Column(name = "content")
	private String content;
	
	@Column(name = "timestamp")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime timestamp;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "from_user", referencedColumnName = "employee_id", insertable = false, updatable = false)
	private EmpBean fromEmployee;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "to_user", referencedColumnName = "employee_id", insertable = false, updatable = false)
	private EmpBean toEmployee;
	
	//自動設置時間戳
	@PrePersist
	protected void onCreat() {
		timestamp = LocalDateTime.now();
	}
	
	public ChatMessage() {
	}

	public ChatMessage(int id, String fromUser, String toUser, String content, LocalDateTime timestamp) {
		this.id = id;
		this.fromUser = fromUser;
		this.toUser = toUser;
		this.content = content;
		this.timestamp = timestamp;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public String getToUser() {
		return toUser;
	}

	public void setToUser(String toUser) {
		this.toUser = toUser;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public EmpBean getFromEmployee() {
		return fromEmployee;
	}

	public void setFromEmployee(EmpBean fromEmployee) {
		this.fromEmployee = fromEmployee;
	}

	public EmpBean getToEmployee() {
		return toEmployee;
	}

	public void setToEmployee(EmpBean toEmployee) {
		this.toEmployee = toEmployee;
	}
	
}
