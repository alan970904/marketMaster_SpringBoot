    package marketMaster.bean.restock;

    import jakarta.persistence.*;
    import lombok.Getter;

    import java.time.LocalDate;

    @Getter
    @Entity
    @Table(name = "restock_details")
    public class RestockDetailsBean {

        @Id
        @Column(name = "detail_id")
        private String detailId;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "restock_id")
        private RestocksBean restock;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "supplier_id")
        private SuppliersBean supplier;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "supplier_product_id")
        private SupplierProductsBean supplierProduct;

        @Column(name = "number_of_restock")
        private int numberOfRestock;

        @Column(name = "restock_total_price")
        private int restockTotalPrice;

        @Column(name = "production_date")
        private LocalDate productionDate;

        @Column(name = "due_date")
        private LocalDate dueDate;

        @Column(name = "restock_date")
        private LocalDate restockDate;

        // Getter 和 Setter 方法省略

        // Constructors
        public RestockDetailsBean() {
        }

        public RestockDetailsBean(String detailId, RestocksBean restock, SuppliersBean supplier,
                                  SupplierProductsBean supplierProduct, int numberOfRestock,
                                  int restockTotalPrice, LocalDate productionDate, LocalDate dueDate, LocalDate restockDate) {
            this.detailId = detailId;
            this.restock = restock;
            this.supplier = supplier;
            this.supplierProduct = supplierProduct;
            this.numberOfRestock = numberOfRestock;
            this.restockTotalPrice = restockTotalPrice;
            this.productionDate = productionDate;
            this.dueDate = dueDate;
            this.restockDate = restockDate;
        }

        public void setDetailId(String detailId) {
            this.detailId = detailId;
        }

        public void setRestock(RestocksBean restock) {
            this.restock = restock;
        }

        public void setSupplier(SuppliersBean supplier) {
            this.supplier = supplier;
        }

        public void setSupplierProduct(SupplierProductsBean supplierProduct) {
            this.supplierProduct = supplierProduct;
        }

        public void setNumberOfRestock(int numberOfRestock) {
            this.numberOfRestock = numberOfRestock;
        }

        public void setRestockTotalPrice(int restockTotalPrice) {
            this.restockTotalPrice = restockTotalPrice;
        }

        public void setProductionDate(LocalDate productionDate) {
            this.productionDate = productionDate;
        }

        public void setDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
        }

        public void setRestockDate(LocalDate restockDate) {
            this.restockDate = restockDate;
        }

        @Override
        public String toString() {
            return "RestockDetailsBean{" +
                    "detailId='" + detailId + '\'' +
                    ", restock=" + restock +
                    ", supplier=" + supplier +
                    ", supplierProduct=" + supplierProduct +
                    ", numberOfRestock=" + numberOfRestock +
                    ", restockTotalPrice=" + restockTotalPrice +
                    ", productionDate=" + productionDate +
                    ", dueDate=" + dueDate +
                    ", restockDate=" + restockDate +
                    '}';
        }
    }
