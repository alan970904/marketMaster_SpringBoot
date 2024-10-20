package marketMaster.bean.checkout;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "return_products")
public class ReturnProductBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "return_id")
    private String returnId;
    
    @Column(name = "original_checkout_id", nullable = false)
    private String originalCheckoutId;

    @Column(name = "original_invoice_number", nullable = false)
    private String originalInvoiceNumber;

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "return_total_price", nullable = false)
    private Integer returnTotalPrice;

    @Column(name = "return_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date returnDate;

    @OneToMany(mappedBy = "returnProduct", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReturnDetailsBean> returnDetails = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_checkout_id", insertable = false, updatable = false)
    private CheckoutBean checkout;

    // 構造函數
    public ReturnProductBean() {
        super();
    }

    public ReturnProductBean(String returnId, String originalCheckoutId, String originalInvoiceNumber, 
                             String employeeId, Integer returnTotalPrice, Date returnDate) {
        this.returnId = returnId;
        this.originalCheckoutId = originalCheckoutId;
        this.originalInvoiceNumber = originalInvoiceNumber;
        this.employeeId = employeeId;
        this.returnTotalPrice = returnTotalPrice;
        this.returnDate = returnDate;
    }

    // Getter 和 Setter 方法
    public String getReturnId() {
        return returnId;
    }

    public void setReturnId(String returnId) {
        this.returnId = returnId;
    }

    public String getOriginalCheckoutId() {
        return originalCheckoutId;
    }

    public void setOriginalCheckoutId(String originalCheckoutId) {
        this.originalCheckoutId = originalCheckoutId;
    }

    public String getOriginalInvoiceNumber() {
        return originalInvoiceNumber;
    }

    public void setOriginalInvoiceNumber(String originalInvoiceNumber) {
        this.originalInvoiceNumber = originalInvoiceNumber;
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

    // 輔助方法
    public void addReturnDetail(ReturnDetailsBean returnDetail) {
        returnDetails.add(returnDetail);
        returnDetail.setReturnProduct(this);
    }

    // equals, hashCode, toString 方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReturnProductBean)) return false;
        ReturnProductBean that = (ReturnProductBean) o;
        return Objects.equals(returnId, that.returnId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(returnId);
    }

    @Override
    public String toString() {
        return "ReturnProductBean{" +
               "returnId='" + returnId + '\'' +
               ", originalCheckoutId='" + originalCheckoutId + '\'' +
               ", originalInvoiceNumber='" + originalInvoiceNumber + '\'' +
               ", employeeId='" + employeeId + '\'' +
               ", returnTotalPrice=" + returnTotalPrice +
               ", returnDate=" + returnDate +
               '}';
    }
}