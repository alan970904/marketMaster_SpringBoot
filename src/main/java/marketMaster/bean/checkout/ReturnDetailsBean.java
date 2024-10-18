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
    @Column(name = "original_checkout_id")
    private String originalCheckoutId;

    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(name = "reason_for_return", nullable = false)
    private String reasonForReturn;

    @Column(name = "number_of_return", nullable = false)
    private Integer numberOfReturn;

    @Column(name = "product_price", nullable = false)
    private Integer productPrice;

    @Column(name = "return_price", nullable = false)
    private Integer returnPrice;

    @Column(name = "return_status", nullable = false)
    private String returnStatus;

    @Column(name = "return_photo")
    private String returnPhoto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "original_checkout_id", referencedColumnName = "checkout_id", insertable = false, updatable = false),
        @JoinColumn(name = "product_id", referencedColumnName = "product_id", insertable = false, updatable = false)
    })
    private CheckoutDetailsBean checkoutDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_id", insertable = false, updatable = false)
    private ReturnProductBean returnProduct;

    // 構造函數、getter 和 setter 方法（已在之前提供）


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

    // equals, hashCode, toString 方法

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReturnDetailsBean)) return false;
        ReturnDetailsBean that = (ReturnDetailsBean) o;
        return Objects.equals(returnId, that.returnId) &&
               Objects.equals(originalCheckoutId, that.originalCheckoutId) &&
               Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(returnId, originalCheckoutId, productId);
    }

    @Override
    public String toString() {
        return "ReturnDetailsBean{" +
               "returnId='" + returnId + '\'' +
               ", originalCheckoutId='" + originalCheckoutId + '\'' +
               ", productId='" + productId + '\'' +
               ", reasonForReturn='" + reasonForReturn + '\'' +
               ", numberOfReturn=" + numberOfReturn +
               ", productPrice=" + productPrice +
               ", returnPrice=" + returnPrice +
               ", returnStatus='" + returnStatus + '\'' +
               ", returnPhoto='" + returnPhoto + '\'' +
               '}';
    }
    
    // 驗證方法
    @PrePersist
    @PreUpdate
    public void validateReturnStatus() {
        if (!"顧客因素".equals(returnStatus) && !"商品外觀損傷".equals(returnStatus) && !"商品品質異常".equals(returnStatus)) {
            throw new IllegalArgumentException("無效的退貨狀態");
        }
    }

    // 內部類定義複合主鍵
    public static class ReturnDetailsId implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String returnId;
        private String originalCheckoutId;
        private String productId;

        public ReturnDetailsId() {}

        public ReturnDetailsId(String returnId, String originalCheckoutId, String productId) {
            this.returnId = returnId;
            this.originalCheckoutId = originalCheckoutId;
            this.productId = productId;
        }

        // getter 和 setter 方法

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ReturnDetailsId)) return false;
            ReturnDetailsId that = (ReturnDetailsId) o;
            return Objects.equals(returnId, that.returnId) &&
                   Objects.equals(originalCheckoutId, that.originalCheckoutId) &&
                   Objects.equals(productId, that.productId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(returnId, originalCheckoutId, productId);
        }
    }
}