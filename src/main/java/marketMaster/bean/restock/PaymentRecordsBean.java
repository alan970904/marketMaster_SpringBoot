package marketMaster.bean.restock;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payment_records")
public class PaymentRecordsBean {

    @Id
    @Column(name = "record_id")
    private String recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private PaymentsBean payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restock_id")
    private RestocksBean restock;

    @Column(name = "payment_amount")
    private int paymentAmount;

    @Override
    public String toString() {
        return "PaymentRecordsBean{" +
                "recordId='" + recordId + '\'' +
                ", paymentId=" + (payment != null ? payment.getPaymentId() : "null") +
                ", restockId=" + (restock != null ? restock.getRestockId() : "null") +
                ", paymentAmount=" + paymentAmount +
                '}';
    }

}
