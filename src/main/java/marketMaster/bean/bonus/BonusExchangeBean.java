package marketMaster.bean.bonus;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import marketMaster.bean.customer.CustomerBean;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "bonus_exchange")
public class BonusExchangeBean {
    @Id
    @Column(name = "exchange_id")
    private String exchangeId;

    @Column(name = "customer_tel")
    private String customerTel;

    @Column(name = "item_id")
    private String itemId;

    @Column(name = "use_points")
    private int usePoints;

    @Column(name = "number_of_exchange")
    private int numberOfExchange;

    @Column(name = "exchange_date")
    private LocalDate exchangeDate;

    // 多對一關聯 - 對應可兌換商品表
    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "item_id", insertable = false, updatable = false)
    private ItemManagementBean exchangeItem; // 對應的可兌換商品對象

    // 多對一關聯 - 對應會員表（假設表格中有此欄位）
    @ManyToOne
    @JoinColumn(name = "customer_tel", referencedColumnName = "customer_tel", insertable = false, updatable = false)
    private CustomerBean customer; // 對應的會員對象

    @Override
    public String toString() {
        return "BonusExchangeBean{" +
                "exchangeId='" + exchangeId + '\'' +
                ", customerTel='" + customerTel + '\'' +
                ", itemId='" + itemId + '\'' +
                ", usePoints=" + usePoints +
                ", numberOfExchange=" + numberOfExchange +
                ", exchangeDate=" + exchangeDate +
                ", exchangeItem=" + exchangeItem +
                ", customer=" + customer +
                '}';
    }
}