    package marketMaster.bean.restock;

    import jakarta.persistence.*;
    import lombok.Getter;
    import lombok.Setter;

    import java.time.LocalDate;
    import java.util.Set;

    @Setter
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

        @Getter
        @Column(name = "price_at_restock")
        private int priceAtRestock;
        @Column(name = "restock_total_price")
        private int restockTotalPrice;

        @Column(name = "production_date")
        private LocalDate productionDate;

        @Column(name = "due_date")
        private LocalDate dueDate;

        @Column(name = "restock_date")
        private LocalDate restockDate;

        // 與 PaymentRecordsBean 的一對多關係
        @OneToMany(mappedBy = "restockDetails")
        private Set<PaymentRecordsBean> paymentRecords;

        // Getter 和 Setter 方法省略

        // Constructors
        public RestockDetailsBean() {
        }

        public RestockDetailsBean(String detailId, RestocksBean restock, SuppliersBean supplier,
                                  SupplierProductsBean supplierProduct, int numberOfRestock,
                                  int priceAtRestock, int restockTotalPrice, LocalDate productionDate, LocalDate dueDate, LocalDate restockDate) {
            this.detailId = detailId;
            this.restock = restock;
            this.supplier = supplier;
            this.supplierProduct = supplierProduct;
            this.numberOfRestock = numberOfRestock;
            this.priceAtRestock = priceAtRestock;
            this.restockTotalPrice = restockTotalPrice;
            this.productionDate = productionDate;
            this.dueDate = dueDate;
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
                    ", priceAtRestock=" + priceAtRestock +
                    ", restockTotalPrice=" + restockTotalPrice +
                    ", productionDate=" + productionDate +
                    ", dueDate=" + dueDate +
                    ", restockDate=" + restockDate +
                    '}';
        }
    }
