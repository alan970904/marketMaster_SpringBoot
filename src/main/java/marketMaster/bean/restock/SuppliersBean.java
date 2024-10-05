    package marketMaster.bean.restock;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.*;
    import java.util.Set;

    @Entity
    @Table(name = "suppliers")
    public class SuppliersBean {

        @Id
        @Column(name = "supplier_id")
        private String supplierId;

        @Column(name = "supplier_name")
        private String supplierName;

        @Column(name = "address")
        private String address;

        @Column(name = "tax_number")
        private String taxNumber;

        @Column(name = "contact_person")
        private String contactPerson;

        @Column(name = "phone")
        private String phone;

        @Column(name = "email")
        private String email;

        @Column(name = "bank_account")
        private String bankAccount;

        // 供應商商品的一對多關係
        @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
        @JsonIgnore
        private Set<SupplierProductsBean> supplierProducts;

        // 供應商帳戶的一對一關係
        @OneToOne(mappedBy = "supplier", cascade = CascadeType.ALL)
        @JsonIgnore
        private SupplierAccountsBean supplierAccount;

        // 進貨明細的一對多關係
        @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
        @JsonIgnore
        private Set<RestockDetailsBean> restockDetails;

        // Getter 和 Setter 方法省略

        // Constructors
        public SuppliersBean() {
        }

        // 全部參數的構造函數
        public SuppliersBean(String supplierId, String supplierName, String address, String taxNumber,
                             String contactPerson, String phone, String email, String bankAccount) {
            this.supplierId = supplierId;
            this.supplierName = supplierName;
            this.address = address;
            this.taxNumber = taxNumber;
            this.contactPerson = contactPerson;
            this.phone = phone;
            this.email = email;
            this.bankAccount = bankAccount;
        }

        public String getSupplierId() {
            return supplierId;
        }

        public void setSupplierId(String supplierId) {
            this.supplierId = supplierId;
        }

        public String getSupplierName() {
            return supplierName;
        }

        public void setSupplierName(String supplierName) {
            this.supplierName = supplierName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getTaxNumber() {
            return taxNumber;
        }

        public void setTaxNumber(String taxNumber) {
            this.taxNumber = taxNumber;
        }

        public String getContactPerson() {
            return contactPerson;
        }

        public void setContactPerson(String contactPerson) {
            this.contactPerson = contactPerson;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getBankAccount() {
            return bankAccount;
        }

        public void setBankAccount(String bankAccount) {
            this.bankAccount = bankAccount;
        }

        public Set<SupplierProductsBean> getSupplierProducts() {
            return supplierProducts;
        }

        public void setSupplierProducts(Set<SupplierProductsBean> supplierProducts) {
            this.supplierProducts = supplierProducts;
        }

        public SupplierAccountsBean getSupplierAccount() {
            return supplierAccount;
        }

        public void setSupplierAccount(SupplierAccountsBean supplierAccount) {
            this.supplierAccount = supplierAccount;
        }

        public Set<RestockDetailsBean> getRestockDetails() {
            return restockDetails;
        }

        public void setRestockDetails(Set<RestockDetailsBean> restockDetails) {
            this.restockDetails = restockDetails;
        }


        @Override
        public String toString() {
            return "SuppliersBean{" +
                    "supplierId='" + supplierId + '\'' +
                    ", supplierName='" + supplierName + '\'' +
                    ", address='" + address + '\'' +
                    ", taxNumber='" + taxNumber + '\'' +
                    ", contactPerson='" + contactPerson + '\'' +
                    ", phone='" + phone + '\'' +
                    ", email='" + email + '\'' +
                    ", bankAccount='" + bankAccount + '\'' +
                    // 避免遞歸調用
                    // ", supplierProducts=" + supplierProducts +
                    // ", supplierAccount=" + supplierAccount +
                    // ", restockDetails=" + restockDetails +
                    '}';
        }
    }
