package marketMaster.bean.checkout;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "return_details")
@IdClass(ReturnDetailsBean.ReturnDetailsId.class)
public class ReturnDetailsBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "return_id")
    private String returnId;

    @Id
    @Column(name = "checkout_id")
    private String checkoutId;

    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(name = "reason_for_return")
    private String reasonForReturn;

    @Column(name = "number_of_return")
    private Integer numberOfReturn;

    @Column(name = "product_price")
    private Integer productPrice;

    @Column(name = "return_price")
    private Integer returnPrice;

    @Column(name = "return_status")
    private String returnStatus;

    @Column(name = "return_photo")
    private String returnPhoto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "checkout_id", referencedColumnName = "CHECKOUT_ID", insertable = false, updatable = false),
        @JoinColumn(name = "product_id", referencedColumnName = "PRODUCT_ID", insertable = false, updatable = false)
    })
    private CheckoutDetailsBean checkoutDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_id", insertable = false, updatable = false)
    private ReturnProductBean returnProduct;

    // Constructors
    public ReturnDetailsBean() {
        super();
    }

    public ReturnDetailsBean(String returnId, String checkoutId, String productId, String reasonForReturn,
                             Integer numberOfReturn, Integer productPrice, Integer returnPrice, 
                             String returnStatus, String returnPhoto) {
        this.returnId = returnId;
        this.checkoutId = checkoutId;
        this.productId = productId;
        this.reasonForReturn = reasonForReturn;
        this.numberOfReturn = numberOfReturn;
        this.productPrice = productPrice;
        this.returnPrice = returnPrice;
        this.returnStatus = returnStatus;
        this.returnPhoto = returnPhoto;
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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getReasonForReturn() {
        return reasonForReturn;
    }

    public void setReasonForReturn(String reasonForReturn) {
        this.reasonForReturn = reasonForReturn;
    }

    public Integer getNumberOfReturn() {
        return numberOfReturn;
    }

    public void setNumberOfReturn(Integer numberOfReturn) {
        this.numberOfReturn = numberOfReturn;
    }
    
    public Integer getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Integer productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getReturnPrice() {
        return returnPrice;
    }

    public void setReturnPrice(Integer returnPrice) {
        this.returnPrice = returnPrice;
    }

    public String getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(String returnStatus) {
        this.returnStatus = returnStatus;
    }

    public String getReturnPhoto() {
        return returnPhoto;
    }

    public void setReturnPhoto(String returnPhoto) {
        this.returnPhoto = returnPhoto;
    }

    public CheckoutDetailsBean getCheckoutDetail() {
        return checkoutDetail;
    }

    public void setCheckoutDetail(CheckoutDetailsBean checkoutDetail) {
        this.checkoutDetail = checkoutDetail;
    }

    public ReturnProductBean getReturnProduct() {
        return returnProduct;
    }

    public void setReturnProduct(ReturnProductBean returnProduct) {
        this.returnProduct = returnProduct;
    }

    // equals, hashCode, toString methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReturnDetailsBean)) return false;
        ReturnDetailsBean that = (ReturnDetailsBean) o;
        return Objects.equals(returnId, that.returnId) &&
               Objects.equals(checkoutId, that.checkoutId) &&
               Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(returnId, checkoutId, productId);
    }

    @Override
    public String toString() {
        return "ReturnDetailsBean{" +
               "returnId='" + returnId + '\'' +
               ", checkoutId='" + checkoutId + '\'' +
               ", productId='" + productId + '\'' +
               ", reasonForReturn='" + reasonForReturn + '\'' +
               ", numberOfReturn=" + numberOfReturn +
               ", productPrice=" + productPrice +
               ", returnPrice=" + returnPrice +
               ", returnStatus='" + returnStatus + '\'' +
               ", returnPhoto='" + returnPhoto + '\'' +
               '}';
    }

    // 內部類定義複合主鍵
    public static class ReturnDetailsId implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String returnId;
        private String checkoutId;
        private String productId;

        public ReturnDetailsId() {}

        public ReturnDetailsId(String returnId, String checkoutId, String productId) {
            this.returnId = returnId;
            this.checkoutId = checkoutId;
            this.productId = productId;
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

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        // equals and hashCode methods
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ReturnDetailsId)) return false;
            ReturnDetailsId that = (ReturnDetailsId) o;
            return Objects.equals(returnId, that.returnId) &&
                   Objects.equals(checkoutId, that.checkoutId) &&
                   Objects.equals(productId, that.productId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(returnId, checkoutId, productId);
        }
    }
}