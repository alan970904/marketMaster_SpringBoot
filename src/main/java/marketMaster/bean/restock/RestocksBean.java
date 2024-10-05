        package marketMaster.bean.restock;

        import jakarta.persistence.*;
        import marketMaster.bean.employee.EmpBean;

        import java.time.LocalDate;
        import java.util.Set;

        @Entity
        @Table(name = "restocks")
        public class RestocksBean {

            @Id
            @Column(name = "restock_id")
            private String restockId;

            @Column(name = "restock_total_price")
            private int restockTotalPrice;

            @Column(name = "restock_date")
            private LocalDate restockDate;

            @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "employee_id")
            private EmpBean employee;

            // 與 RestockDetailsBean 的一對多關係
            @OneToMany(mappedBy = "restock", cascade = CascadeType.ALL)
            private Set<RestockDetailsBean> restockDetails;

            // 與 PaymentRecordsBean 的一對多關係
            @OneToMany(mappedBy = "restock", cascade = CascadeType.ALL)
            private Set<PaymentRecordsBean> paymentRecords;

            // Getter 和 Setter 方法省略

            // Constructors
            public RestocksBean() {
            }

            public RestocksBean(String restockId, int restockTotalPrice, LocalDate restockDate, EmpBean employee) {
                this.restockId = restockId;
                this.restockTotalPrice = restockTotalPrice;
                this.restockDate = restockDate;
                this.employee = employee;
            }

            public Set<PaymentRecordsBean> getPaymentRecords() {
                return paymentRecords;
            }

            public void setPaymentRecords(Set<PaymentRecordsBean> paymentRecords) {
                this.paymentRecords = paymentRecords;
            }

            public Set<RestockDetailsBean> getRestockDetails() {
                return restockDetails;
            }

            public void setRestockDetails(Set<RestockDetailsBean> restockDetails) {
                this.restockDetails = restockDetails;
            }

            public EmpBean getEmployee() {
                return employee;
            }

            public void setEmployee(EmpBean employee) {
                this.employee = employee;
            }

            public LocalDate getRestockDate() {
                return restockDate;
            }

            public void setRestockDate(LocalDate restockDate) {
                this.restockDate = restockDate;
            }

            public int getRestockTotalPrice() {
                return restockTotalPrice;
            }

            public void setRestockTotalPrice(int restockTotalPrice) {
                this.restockTotalPrice = restockTotalPrice;
            }

            public String getRestockId() {
                return restockId;
            }

            public void setRestockId(String restockId) {
                this.restockId = restockId;
            }

            @Override
            public String toString() {
                return "RestocksBean{" +
                        "restockId='" + restockId + '\'' +
                        ", restockTotalPrice=" + restockTotalPrice +
                        ", restockDate=" + restockDate +
                        ", employee=" + employee +
                        ", restockDetails=" + restockDetails +
                        ", paymentRecords=" + paymentRecords +
                        '}';
            }
        }
