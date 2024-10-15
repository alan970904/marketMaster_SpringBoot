package marketMaster.bean.askForLeave;

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
@Table(name = "leave_category")
public class LeaveCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer CategoryId;
    
 

    @Column(name = "leave_type")
    private String leaveType;  

    @Column(name = "limit_hours")
    private int LimitHours;
    
	public LeaveCategory() {
	}
	
	

	public LeaveCategory(String leaveType, int limitHours) {
		this.leaveType = leaveType;
		LimitHours = limitHours;
	}



	public Integer getCategoryId() {
		return CategoryId;
	}

	public void setCategoryId(Integer categoryId) {
		CategoryId = categoryId;
	}

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public int getLimitHours() {
		return LimitHours;
	}

	public void setLimitHours(int limitHours) {
		LimitHours = limitHours;
	}  
    
    

}
