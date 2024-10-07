    package marketMaster.bean.restock;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.*;
    import lombok.Getter;
    import lombok.Setter;
    import marketMaster.bean.product.ProductBean;


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

        // Getter 和 Setter 方法省略

        public String getSupplierProductId() {
            return supplierProductId;
        }

        public void setSupplierProductId(String supplierProductId) {
            this.supplierProductId = supplierProductId;
        }

        public SuppliersBean getSupplier() {
            return supplier;
        }

        public void setSupplier(SuppliersBean supplier) {
            this.supplier = supplier;
        }

        public ProductBean getProduct() {
            return product;
        }

        public void setProduct(ProductBean product) {
            this.product = product;
        }

        public int getProductPrice() {
            return productPrice;
        }

        public void setProductPrice(int productPrice) {
            this.productPrice = productPrice;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
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
                    ", supplierId=" + (supplier != null ? supplier.getSupplierId() : "null") +
                    ", productId=" + (product != null ? product.getProductId() : "null") +
                    ", productPrice=" + productPrice +
                    ", status=" + status +
                    '}';
        }
    }
