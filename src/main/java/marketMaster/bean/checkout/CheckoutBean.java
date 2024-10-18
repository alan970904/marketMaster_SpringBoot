package marketMaster.bean.checkout;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "checkout")
public class CheckoutBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "checkout_id")
    private String checkoutId;

    @Column(name = "invoice_number", nullable = false)
    private String invoiceNumber;

    @Column(name = "customer_tel")
    private String customerTel;

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "checkout_total_price", nullable = false)
    private int checkoutTotalPrice;

    @Temporal(TemporalType.DATE)
    @Column(name = "checkout_date", nullable = false)
    private Date checkoutDate;

    @Column(name = "bonus_points")
    private Integer bonusPoints;

    @Temporal(TemporalType.DATE)
    @Column(name = "points_due_date")
    private Date pointsDueDate;

    @Column(name = "checkout_status", nullable = false)
    private String checkoutStatus;

    @Column(name = "related_return_id")
    private String relatedReturnId;

    @OneToMany(mappedBy = "checkout", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CheckoutDetailsBean> checkoutDetails = new ArrayList<>();

    @OneToMany(mappedBy = "checkout", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReturnProductBean> returnProducts = new ArrayList<>();

    // Constructors
    public CheckoutBean() {
        super();
    }

    public CheckoutBean(String checkoutId, String invoiceNumber, String customerTel, String employeeId,
                        int checkoutTotalPrice, Date checkoutDate, Integer bonusPoints, Date pointsDueDate,
                        String checkoutStatus, String relatedReturnId) {
        this.checkoutId = checkoutId;
        this.invoiceNumber = invoiceNumber;
        this.customerTel = customerTel;
        this.employeeId = employeeId;
        this.checkoutTotalPrice = checkoutTotalPrice;
        this.checkoutDate = checkoutDate;
        this.bonusPoints = bonusPoints;
        this.pointsDueDate = pointsDueDate;
        this.checkoutStatus = checkoutStatus;
        this.relatedReturnId = relatedReturnId;
    }

    // Getters and Setters
    public String getCheckoutId() {
        return checkoutId;
    }

    public void setCheckoutId(String checkoutId) {
        this.checkoutId = checkoutId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getCustomerTel() {
        return customerTel;
    }

    public void setCustomerTel(String customerTel) {
        this.customerTel = customerTel;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public int getCheckoutTotalPrice() {
        return checkoutTotalPrice;
    }

    public void setCheckoutTotalPrice(int checkoutTotalPrice) {
        this.checkoutTotalPrice = checkoutTotalPrice;
    }

    public Date getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(Date checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public Integer getBonusPoints() {
        return bonusPoints;
    }

    public void setBonusPoints(Integer bonusPoints) {
        this.bonusPoints = bonusPoints;
    }

    public Date getPointsDueDate() {
        return pointsDueDate;
    }

    public void setPointsDueDate(Date pointsDueDate) {
        this.pointsDueDate = pointsDueDate;
    }

    public String getCheckoutStatus() {
        return checkoutStatus;
    }

    public void setCheckoutStatus(String checkoutStatus) {
        this.checkoutStatus = checkoutStatus;
    }

    public String getRelatedReturnId() {
        return relatedReturnId;
    }

    public void setRelatedReturnId(String relatedReturnId) {
        this.relatedReturnId = relatedReturnId;
    }

    public List<CheckoutDetailsBean> getCheckoutDetails() {
        return checkoutDetails;
    }

    public void setCheckoutDetails(List<CheckoutDetailsBean> checkoutDetails) {
        this.checkoutDetails = checkoutDetails;
    }

    public List<ReturnProductBean> getReturnProducts() {
        return returnProducts;
    }

    public void setReturnProducts(List<ReturnProductBean> returnProducts) {
        this.returnProducts = returnProducts;
    }

    // Helper methods
    public void addCheckoutDetail(CheckoutDetailsBean detail) {
        checkoutDetails.add(detail);
        detail.setCheckout(this);
    }

    public void addReturnProduct(ReturnProductBean returnProduct) {
        returnProducts.add(returnProduct);
        returnProduct.setCheckout(this);
    }

    // equals, hashCode, and toString methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CheckoutBean)) return false;
        CheckoutBean that = (CheckoutBean) o;
        return Objects.equals(checkoutId, that.checkoutId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkoutId);
    }

    @Override
    public String toString() {
        return "CheckoutBean{" +
                "checkoutId='" + checkoutId + '\'' +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                ", customerTel='" + customerTel + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", checkoutTotalPrice=" + checkoutTotalPrice +
                ", checkoutDate=" + checkoutDate +
                ", bonusPoints=" + bonusPoints +
                ", pointsDueDate=" + pointsDueDate +
                ", checkoutStatus='" + checkoutStatus + '\'' +
                ", relatedReturnId='" + relatedReturnId + '\'' +
                '}';
    }
}