package marketMaster.bean.checkout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.*;

@Entity @Table(name = "checkout")
public class CheckoutBean implements Serializable  {
	private static final long serialVersionUID = 1L;
	
	@Id @Column(name = "CHECKOUT_ID")
	private String checkoutId;
	
	@Column(name = "CUSTOMER_TEL")
	private String customerTel;
	
	@Column(name = "EMPLOYEE_ID")
	private String employeeId;
	
	@Column(name = "CHECKOUT_TOTAL_PRICE")
	private int checkoutTotalPrice;
	
	@Temporal(TemporalType.DATE)
    @Column(name = "CHECKOUT_DATE")
	private Date checkoutDate;
	
	@Column(name = "BONUS_POINTS")
	private int bonusPoints;
	
	@Temporal(TemporalType.DATE)
    @Column(name = "POINTS_DUE_DATE")
	private Date pointsDueDate;
	
	@OneToMany(mappedBy = "checkout", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CheckoutDetailsBean> checkoutDetails = new ArrayList<>();

	// Constructors, Getters, and Setters

	public CheckoutBean() {
		super();
	}

	public CheckoutBean(String checkoutId, String customerTel, String employeeId, int checkoutTotalPrice,
			Date checkoutDate, int bonusPoints, Date pointsDueDate, List<CheckoutDetailsBean> checkoutDetails) {
		super();
		this.checkoutId = checkoutId;
		this.customerTel = customerTel;
		this.employeeId = employeeId;
		this.checkoutTotalPrice = checkoutTotalPrice;
		this.checkoutDate = checkoutDate;
		this.bonusPoints = bonusPoints;
		this.pointsDueDate = pointsDueDate;
		this.checkoutDetails = checkoutDetails;
	}

	public String getCheckoutId() {
		return checkoutId;
	}

	public void setCheckoutId(String checkoutId) {
		this.checkoutId = checkoutId;
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

	public int getBonusPoints() {
		return bonusPoints;
	}

	public void setBonusPoints(int bonusPoints) {
		this.bonusPoints = bonusPoints;
	}

	public Date getPointsDueDate() {
		return pointsDueDate;
	}

	public void setPointsDueDate(Date pointsDueDate) {
		this.pointsDueDate = pointsDueDate;
	}

	public List<CheckoutDetailsBean> getCheckoutDetails() {
		return checkoutDetails;
	}

	public void setCheckoutDetails(List<CheckoutDetailsBean> checkoutDetails) {
		this.checkoutDetails = checkoutDetails;
	}

	public void addCheckoutDetail(CheckoutDetailsBean detail) {
		if (checkoutDetails == null) {
			checkoutDetails = new ArrayList<>();
		}
		checkoutDetails.add(detail);
		detail.setCheckout(this);
	}
	
	// 添加 equals 和 hashCode 方法
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

    // 添加 toString 方法
    @Override
    public String toString() {
        return "CheckoutBean{" +
               "checkoutId='" + checkoutId + '\'' +
               ", customerTel='" + customerTel + '\'' +
               ", employeeId='" + employeeId + '\'' +
               ", checkoutTotalPrice=" + checkoutTotalPrice +
               ", checkoutDate=" + checkoutDate +
               ", bonusPoints=" + bonusPoints +
               ", pointsDueDate=" + pointsDueDate +
               '}';
    }
}