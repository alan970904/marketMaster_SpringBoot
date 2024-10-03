package marketMaster.bean.restock;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import marketMaster.bean.employee.EmpBean;

@Entity
@Table(name = "restocks")
public class RestockBean {

    @Id
    @Column(name = "restock_id")
    private String restockId;

    @Column(name = "restock_total_price")
    private int restockTotalPrice;

    @Column(name = "restock_date")
    private LocalDate restockDate;

    @OneToMany(mappedBy = "restock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestockDetailsBean> restockDetails = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private EmpBean employee;

    // Constructors
    public RestockBean() {}

    public RestockBean(String restockId, EmpBean employee, int restockTotalPrice, LocalDate restockDate) {
        this.restockId = restockId;
        this.employee = employee;
        this.restockTotalPrice = restockTotalPrice;
        this.restockDate = restockDate;
    }

    // Getters and Setters
    public String getRestockId() {
        return restockId;
    }

    public void setRestockId(String restockId) {
        this.restockId = restockId;
    }

    public int getRestockTotalPrice() {
        return restockTotalPrice;
    }

    public void setRestockTotalPrice(int restockTotalPrice) {
        this.restockTotalPrice = restockTotalPrice;
    }

    public LocalDate getRestockDate() {
        return restockDate;
    }

    public void setRestockDate(LocalDate restockDate) {
        this.restockDate = restockDate;
    }

    public List<RestockDetailsBean> getRestockDetails() {
        return restockDetails;
    }

    public void setRestockDetails(List<RestockDetailsBean> restockDetails) {
        this.restockDetails = restockDetails;
    }

    public EmpBean getEmployee() {
        return employee;
    }

    public void setEmployee(EmpBean employee) {
        this.employee = employee;
    }

    // 添加便利方法來管理雙向關係
    public void addRestockDetails(RestockDetailsBean details) {
        restockDetails.add(details);
        details.setRestock(this);
    }

    public void removeRestockDetails(RestockDetailsBean details) {
        restockDetails.remove(details);
        details.setRestock(null);
    }

}
