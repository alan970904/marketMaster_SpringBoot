package marketMaster.bean.restock;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
public class PaymentsBean {

    @Id
    @Column(name = "payment_id")
    private String paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private SupplierAccountsBean supplierAccounts;

    @OneToMany(mappedBy = "payment",cascade = CascadeType.ALL)
    private Set<PaymentRecordsBean> paymentRecords;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "total_amount")
    private int totalAmount;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Override
    public String toString() {
        return "PaymentsBean{" +
                "paymentId='" + paymentId + '\'' +
                ", accountId=" + (supplierAccounts != null ? supplierAccounts.getAccountId() : "null") +
                ", paymentDate=" + paymentDate +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", totalAmount=" + totalAmount +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }

}
