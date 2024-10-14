package marketMaster.bean.checkout;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "return_products")
public class ReturnProductBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "return_id")
    private String returnId;
    
    @Column(name = "checkout_id")
    private String checkoutId;

    @Column(name = "employee_id")
    private String employeeId;

    @Column(name = "return_total_price")
    private Integer returnTotalPrice;

    @Column(name = "return_date")
    @Temporal(TemporalType.DATE)
    private Date returnDate;

    @OneToMany(mappedBy = "returnProduct", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReturnDetailsBean> returnDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checkout_id", insertable = false, updatable = false)
    private CheckoutBean checkout;

    public ReturnProductBean() {
        super();
    }

    public ReturnProductBean(String returnId, String checkoutId, String employeeId, Integer returnTotalPrice, Date returnDate) {
        this.returnId = returnId;
        this.checkoutId = checkoutId;
        this.employeeId = employeeId;
        this.returnTotalPrice = returnTotalPrice;
        this.returnDate = returnDate;
        this.returnDetails = new ArrayList<>();
    }

    // Getters and setters
    public String getReturnId() {
        return returnId;
    }

    public void setReturnId(String returnId) {
        this.returnId = returnId;
    }

    public String getCheckoutId() {
        return checkoutId;
    }

    public void setCheckoutId(String checkoutId) {
        this.checkoutId = checkoutId;
    }
    
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getReturnTotalPrice() {
        return returnTotalPrice;
    }

    public void setReturnTotalPrice(Integer returnTotalPrice) {
        this.returnTotalPrice = returnTotalPrice;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public List<ReturnDetailsBean> getReturnDetails() {
        return returnDetails;
    }

    public void setReturnDetails(List<ReturnDetailsBean> returnDetails) {
        this.returnDetails = returnDetails;
    }
    
    public CheckoutBean getCheckout() {
        return checkout;
    }

    public void setCheckout(CheckoutBean checkout) {
        this.checkout = checkout;
    }

    public void addReturnDetail(ReturnDetailsBean returnDetail) {
        if (returnDetails == null) {
            returnDetails = new ArrayList<>();
        }
        returnDetails.add(returnDetail);
        returnDetail.setReturnProduct(this);
    }

    @Override
    public String toString() {
        return "ReturnProductBean [returnId=" + returnId + ", employeeId=" + employeeId + ", returnTotalPrice="
                + returnTotalPrice + ", returnDate=" + returnDate + "]";
    }
}
