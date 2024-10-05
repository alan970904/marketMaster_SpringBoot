    package marketMaster.bean.restock;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.*;

    @Entity
    @Table(name = "supplier_accounts")
    public class SupplierAccountsBean {

        @Id
        @Column(name = "account_id")
        private String accountId;

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "supplier_id")
        @JsonIgnore
        private SuppliersBean supplier;

        @Column(name = "total_amount")
        private int totalAmount;

        @Column(name = "paid_amount")
        private int paidAmount;

        @Column(name = "unpaid_amount")
        private int unpaidAmount;

        // Getter 和 Setter 方法省略

        // Constructors
        public SupplierAccountsBean() {
        }

        public SupplierAccountsBean(String accountId, SuppliersBean supplier, int totalAmount, int paidAmount, int unpaidAmount) {
            this.accountId = accountId;
            this.supplier = supplier;
            this.totalAmount = totalAmount;
            this.paidAmount = paidAmount;
            this.unpaidAmount = unpaidAmount;
        }

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public SuppliersBean getSupplier() {
            return supplier;
        }

        public void setSupplier(SuppliersBean supplier) {
            this.supplier = supplier;
        }

        public int getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(int totalAmount) {
            this.totalAmount = totalAmount;
        }

        public int getPaidAmount() {
            return paidAmount;
        }

        public void setPaidAmount(int paidAmount) {
            this.paidAmount = paidAmount;
        }

        public int getUnpaidAmount() {
            return unpaidAmount;
        }

        public void setUnpaidAmount(int unpaidAmount) {
            this.unpaidAmount = unpaidAmount;
        }

        @Override
        public String toString() {
            return "SupplierAccountsBean{" +
                    "accountId='" + accountId + '\'' +
                    // 避免遞歸調用
                    // ", supplier=" + supplier +
                    ", totalAmount=" + totalAmount +
                    ", paidAmount=" + paidAmount +
                    ", unpaidAmount=" + unpaidAmount +
                    '}';
        }
    }
