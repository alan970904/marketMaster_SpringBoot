package marketMaster.bean.checkout;

import jakarta.persistence.*;
import marketMaster.bean.product.ProductBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity 
@Table(name = "checkout_details")
@IdClass(CheckoutDetailsBean.CheckoutDetailsId.class)
public class CheckoutDetailsBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "checkout_id")
    private String checkoutId;

    @Id
    @Column(name = "product_id")
    private String productId;
    
    @Column(name = "number_of_checkout", nullable = false)
    private int numberOfCheckout;
    
    @Column(name = "product_price", nullable = false)
    private int productPrice;
    
    @Column(name = "checkout_price", nullable = false)
    private int checkoutPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checkout_id", insertable = false, updatable = false)
    @JsonIgnore
    private CheckoutBean checkout;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private ProductBean product;
    
    @OneToMany(mappedBy = "checkoutDetail", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReturnDetailsBean> returnDetails = new ArrayList<>();

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

    public ProductBean getProduct() {
        return product;
    }

    public void setProduct(ProductBean product) {
        this.product = product;
    }
    
    public List<ReturnDetailsBean> getReturnDetails() {
        return returnDetails;
    }

    public void setReturnDetails(List<ReturnDetailsBean> returnDetails) {
        this.returnDetails = returnDetails;
    }

    // Helper method
    public void addReturnDetail(ReturnDetailsBean returnDetail) {
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

    // Inner class for composite primary key
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