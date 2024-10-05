package marketMaster.bean.restock;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "payment_records")
public class PaymentRecordsBean {

    @Id
    @Column(name = "payment_id")
    private String paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restock_id")
    private RestocksBean restock;

    @Column(name = "payment_amount")
    private int paymentAmount;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "payment_status")
    private String paymentStatus;

    // Getter 和 Setter 方法省略

    // Constructors
    public PaymentRecordsBean() {
    }

    public PaymentRecordsBean(String paymentId, RestocksBean restock, int paymentAmount,
                              LocalDate paymentDate, String paymentMethod, String paymentStatus) {
        this.paymentId = paymentId;
        this.restock = restock;
        this.paymentAmount = paymentAmount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public RestocksBean getRestock() {
        return restock;
    }

    public void setRestock(RestocksBean restock) {
        this.restock = restock;
    }

    public int getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(int paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Override
    public String toString() {
        return "PaymentRecordsBean{" +
                "paymentId='" + paymentId + '\'' +
                ", restock=" + restock +
                ", paymentAmount=" + paymentAmount +
                ", paymentDate=" + paymentDate +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}
