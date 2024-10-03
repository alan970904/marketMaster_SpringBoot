package marketMaster.bean.checkout;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity 
@Table(name = "checkout_details")
@IdClass(CheckoutDetailsBean.CheckoutDetailsId.class)
public class CheckoutDetailsBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "CHECKOUT_ID")
    private String checkoutId;

    @Id
    @Column(name = "PRODUCT_ID")
    private String productId;
    
    @Column(name = "NUMBER_OF_CHECKOUT")
    private int numberOfCheckout;
    
    @Column(name = "PRODUCT_PRICE")
    private int productPrice;
    
    @Column(name = "CHECKOUT_PRICE")
    private int checkoutPrice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CHECKOUT_ID", insertable = false, updatable = false)
    private CheckoutBean checkout;

    @OneToMany(mappedBy = "checkoutDetail", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReturnDetailsBean> returnDetails;

    // Constructors
    public CheckoutDetailsBean() {
        super();
    }

    public CheckoutDetailsBean(String checkoutId, String productId, int numberOfCheckout, int productPrice, int checkoutPrice) {
        this.checkoutId = checkoutId;
        this.productId = productId;
        this.numberOfCheckout = numberOfCheckout;
        this.productPrice = productPrice;
        this.checkoutPrice = checkoutPrice;
        this.returnDetails = new ArrayList<>();
    }

    // Getters and setters
    public String getCheckoutId() {
        return checkoutId;
    }

    public void setCheckoutId(String checkoutId) {
        this.checkoutId = checkoutId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getNumberOfCheckout() {
        return numberOfCheckout;
    }

    public void setNumberOfCheckout(int numberOfCheckout) {
        this.numberOfCheckout = numberOfCheckout;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public int getCheckoutPrice() {
        return checkoutPrice;
    }

    public void setCheckoutPrice(int checkoutPrice) {
        this.checkoutPrice = checkoutPrice;
    }

    public CheckoutBean getCheckout() {
        return checkout;
    }

    public void setCheckout(CheckoutBean checkout) {
        this.checkout = checkout;
    }

    public List<ReturnDetailsBean> getReturnDetails() {
        return returnDetails;
    }

    public void setReturnDetails(List<ReturnDetailsBean> returnDetails) {
        this.returnDetails = returnDetails;
    }

    public void addReturnDetail(ReturnDetailsBean returnDetail) {
        if (returnDetails == null) {
            returnDetails = new ArrayList<>();
        }
        returnDetails.add(returnDetail);
        returnDetail.setCheckoutDetail(this);
    }

    // equals, hashCode, toString methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CheckoutDetailsBean)) return false;
        CheckoutDetailsBean that = (CheckoutDetailsBean) o;
        return Objects.equals(checkoutId, that.checkoutId) &&
               Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkoutId, productId);
    }

    @Override
    public String toString() {
        return "CheckoutDetailsBean{" +
               "checkoutId='" + checkoutId + '\'' +
               ", productId='" + productId + '\'' +
               ", numberOfCheckout=" + numberOfCheckout +
               ", productPrice=" + productPrice +
               ", checkoutPrice=" + checkoutPrice +
               '}';
    }

    // 內部類定義複合主鍵
    public static class CheckoutDetailsId implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private String checkoutId;
        private String productId;

        public CheckoutDetailsId() {}

        public CheckoutDetailsId(String checkoutId, String productId) {
            this.checkoutId = checkoutId;
            this.productId = productId;
        }

        // equals and hashCode methods
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CheckoutDetailsId)) return false;
            CheckoutDetailsId that = (CheckoutDetailsId) o;
            return Objects.equals(checkoutId, that.checkoutId) &&
                   Objects.equals(productId, that.productId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(checkoutId, productId);
        }
    }
}