package marketMaster.bean.restock;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import marketMaster.bean.restock.PaymentsBean;
import marketMaster.bean.restock.SuppliersBean;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @OneToMany(mappedBy = "supplierAccounts", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<PaymentsBean> payments;

    @Column(name = "total_amount")
    private int totalAmount;

    @Column(name = "paid_amount")
    private int paidAmount;

    @Column(name = "unpaid_amount")
    private int unpaidAmount;

    @Override
    public String toString() {
        return "SupplierAccountsBean{" +
                "accountId='" + accountId + '\'' +
                // ", supplier=" + supplier +
                ", totalAmount=" + totalAmount +
                ", paidAmount=" + paidAmount +
                ", unpaidAmount=" + unpaidAmount +
                '}';
    }


}
