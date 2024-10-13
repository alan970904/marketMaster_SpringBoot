package marketMaster.bean.restock;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import marketMaster.bean.product.ProductBean;

@Setter
@Getter
@Entity
@Table(name = "supplier_products")
public class SupplierProductsBean {

    @Id
    @Column(name = "supplier_product_id")
    private String supplierProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    @JsonIgnore
    private SuppliersBean supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private ProductBean product;

    @Column(name = "product_price")
    private int productPrice;

    @Column(name = "status")
    private int status;

    // 添加临时字段
    @Transient
    private String supplierId;

    @Transient
    private String productId;

    // Getter 和 Setter 方法

    public String getSupplierId() {
        if (supplier != null) {
            return supplier.getSupplierId();
        }
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
        if (this.supplier == null) {
            this.supplier = new SuppliersBean();
        }
        this.supplier.setSupplierId(supplierId);
    }

    public String getProductId() {
        if (product != null) {
            return product.getProductId();
        }
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
        if (this.product == null) {
            this.product = new ProductBean();
        }
        this.product.setProductId(productId);
    }

    // Constructors
    public SupplierProductsBean() {
    }

    public SupplierProductsBean(String supplierProductId, SuppliersBean supplier, ProductBean product,
                                int productPrice, int status) {
        this.supplierProductId = supplierProductId;
        this.supplier = supplier;
        this.product = product;
        this.productPrice = productPrice;
        this.status = status;
    }

    @Override
    public String toString() {
        return "SupplierProductsBean{" +
                "supplierProductId='" + supplierProductId + '\'' +
                ", supplierId='" + getSupplierId() + '\'' +
                ", productId='" + getProductId() + '\'' +
                ", productPrice=" + productPrice +
                ", status=" + status +
                '}';
    }
}
