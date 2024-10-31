package marketMaster.bean.bonus;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import marketMaster.bean.customer.CustomerBean;
import marketMaster.bean.checkout.CheckoutBean;

@Entity
@Data
@Table(name = "points_history")
public class PointsHistoryBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "points_history_id")
    private int pointsHistoryId;

    @Column(name = "customer_tel")
    private String customerTel;

    @Column(name = "checkout_id")
    private String checkoutId;

    @Column(name = "exchange_id")
    private String exchangeId;

    @Column(name = "points_change")
    private int pointsChange;

    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @Column(name = "transaction_type")
    private String transactionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_tel", referencedColumnName = "customer_tel", insertable = false, updatable = false)
    private CustomerBean customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checkout_id", referencedColumnName = "CHECKOUT_ID", insertable = false, updatable = false)
    private CheckoutBean checkout;

    @Override
    public String toString() {
        return "PointsHistoryBean{" +
                "pointsHistoryId=" + pointsHistoryId +
                ", customerTel='" + customerTel + '\'' +
                ", checkoutId='" + checkoutId + '\'' +
                ", exchangeId='" + exchangeId + '\'' +
                ", pointsChange=" + pointsChange +
                ", transactionDate=" + transactionDate +
                ", transactionType='" + transactionType + '\'' +
                '}';
    }
}