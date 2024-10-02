package marketMaster.bean.employee;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer")
public class CustomerBean implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "customer_tel")
    private String customerTel;
    
    @Column(name = "customer_name")
    private String customerName;
    
    @Column(name = "customer_email")
    private String customerEmail;
    
    @Column(name = "date_of_registration")
    private LocalDate dateOfRegistration;
    
    @Column(name = "total_points")
    private int totalPoints;

    public CustomerBean() {
    }

    public CustomerBean(String customerTel, String customerName, String customerEmail, LocalDate dateOfRegistration, int totalPoints) {
        this.customerTel = customerTel;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.dateOfRegistration = dateOfRegistration;
        this.totalPoints = totalPoints;
    }

    // Getters and Setters
    public String getCustomerTel() {
        return customerTel;
    }

    public void setCustomerTel(String customerTel) {
        this.customerTel = customerTel;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public LocalDate getDateOfRegistration() {
        return dateOfRegistration;
    }

    public void setDateOfRegistration(LocalDate dateOfRegistration) {
        this.dateOfRegistration = dateOfRegistration;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }
}