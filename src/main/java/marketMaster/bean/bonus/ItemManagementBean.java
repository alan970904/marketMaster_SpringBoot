package marketMaster.bean.bonus;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;
import marketMaster.bean.product.ProductBean;

@Entity
@Data
@Table(name = "item_management")
public class ItemManagementBean {

    @Id
    @Column(name = "item_id")
    private String itemId;

    @Setter
    @Column(name = "product_id")
    private String productId;

    @Column(name = "item_points")
    private int itemPoints;

    @Column(name ="item_maximum" ) // 可兌換數量
    private int itemMaximum;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_active")
    private boolean active;

    // 多對一關聯 - 對應商品表
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id", insertable = false, updatable = false)
    private ProductBean product; // 對應的商品對象

    // 一對多關聯 - 對應紅利兌換表
    @OneToMany(mappedBy = "exchangeItem")
    private List<BonusExchangeBean> bonusExchanges;

    @Override
    public String toString() {
        return "ExchangeItemManagementBean{" +
                "itemId='" + itemId + '\'' +
                ", productId='" + productId + '\'' +
                ", itemPoints=" + itemPoints +
                ", itemMaximum=" + itemMaximum +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", active=" + active +
                ", product=" + product +
                ", bonusExchanges=" + bonusExchanges +
                '}';
    }
}
