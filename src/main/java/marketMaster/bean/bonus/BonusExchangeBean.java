package marketMaster.bean.bonus;
import java.time.LocalDate;

import marketMaster.bean.customer.CustomerBean;
import marketMaster.bean.product.ProductBean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bonus_exchange")
public class BonusExchangeBean implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
    @Id
    @Column(name="exchange_id")
    private String exchangeId;

    @ManyToOne
    @JoinColumn(name = "customer_tel", insertable = false, updatable = false)
    private CustomerBean customer;

    @Column(name = "customer_tel")
    private String customerTel;

    @ManyToOne
    @JoinColumn(name="product_id", insertable = false, updatable = false)
    private ProductBean product;

    @Column(name = "product_id")
    private String productId;

    @Column(name="use_points")
    private int usePoints;

    @Column(name="number_of_exchange")
    private int numberOfExchange;

    @Column(name="exchange_date")
    private LocalDate exchangeDate;  // 將類型從 Date 改為 LocalDate
    
    
 // Constructors, Getters, and Setters
    public BonusExchangeBean() {}

    public BonusExchangeBean(String exchangeId, String customerTel, String productId,
                         int usePoints, int numberOfExchange, LocalDate  exchangeDate) {
        this.exchangeId = exchangeId;
        this.customerTel = customerTel;
        this.productId = productId;
        this.usePoints = usePoints;
        this.numberOfExchange = numberOfExchange;
        this.exchangeDate = exchangeDate;
    }

    // Getters and Setters for each field
    public String getExchangeId() { return exchangeId; }
    public void setExchangeId(String exchangeId) { this.exchangeId = exchangeId; }
    public String getCustomerTel() { return customerTel; }
    public void setCustomerTel(String customerTel) { this.customerTel = customerTel; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public int getUsePoints() { return usePoints; }
    public void setUsePoints(int usePoints) { this.usePoints = usePoints; }
    public int getNumberOfExchange() { return numberOfExchange; }
    public void setNumberOfExchange(int numberOfExchange) { this.numberOfExchange = numberOfExchange; }
    public LocalDate  getExchangeDate() { return exchangeDate; }
    public void setExchangeDate(LocalDate  exchangeDate) { this.exchangeDate = exchangeDate; }
    // 添加 customer 和 product 的 getter 和 setter
    public CustomerBean getCustomer() { return customer; }
    public void setCustomer(CustomerBean customer) { this.customer = customer; }
    public ProductBean getProduct() { return product; }
    public void setProduct(ProductBean product) { this.product = product; }
    @Override
    public String toString() {
        return "BonusExchangeBean [exchangeId=" + exchangeId + ", customerTel=" + customerTel + ", productId="
                + productId + ", usePoints=" + usePoints + ", numberOfExchange=" + numberOfExchange + ", exchangeDate="
                + exchangeDate + ", customer=" + customer + ", product=" + product + "]";
    }
    
}