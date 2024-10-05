package marketMaster.bean.employee;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "ranklevel")
public class RankLevelBean implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "position_id")
    private String positionId;

    @Column(name = "position_name")
    private String positionName;

    @Column(name = "limits_of_authority")
    private int limitsOfAuthority;

    @Column(name = "salary_level")
    private String salaryLevel;

    @Transient
    private int activeEmployeeCount;

    @Transient
    private int totalEmployeeCount;

    @OneToMany(mappedBy = "rankLevel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmpBean> employees = new ArrayList<>();

    public RankLevelBean() {
    }

    public RankLevelBean(String positionId, String positionName, int limitsOfAuthority, int activeEmployeeCount, int totalEmployeeCount) {
        this.positionId = positionId;
        this.positionName = positionName;
        this.limitsOfAuthority = limitsOfAuthority;
        this.activeEmployeeCount = activeEmployeeCount;
        this.totalEmployeeCount = totalEmployeeCount;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public int getLimitsOfAuthority() {
        return limitsOfAuthority;
    }

    public void setLimitsOfAuthority(int limitsOfAuthority) {
        this.limitsOfAuthority = limitsOfAuthority;
    }

    public String getSalaryLevel() {
        return salaryLevel;
    }

    public void setSalaryLevel(String salaryLevel) {
        this.salaryLevel = salaryLevel;
    }

    public int getActiveEmployeeCount() {
        return activeEmployeeCount;
    }

    public void setActiveEmployeeCount(int activeEmployeeCount) {
        this.activeEmployeeCount = activeEmployeeCount;
    }

    public int getTotalEmployeeCount() {
        return totalEmployeeCount;
    }

    public void setTotalEmployeeCount(int totalEmployeeCount) {
        this.totalEmployeeCount = totalEmployeeCount;
    }
}
