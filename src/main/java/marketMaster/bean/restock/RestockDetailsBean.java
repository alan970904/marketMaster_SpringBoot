package marketMaster.bean.restock;

import jakarta.persistence.*;
import marketMaster.bean.product.ProductBean;
import java.time.LocalDate;

@Entity
@Table(name = "restock_details")
public class RestockDetailsBean {

    @EmbeddedId
    private RestockDetailsId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("restockId") // 對應到 RestockDetailsId 中的 restockId
    @JoinColumn(name = "restock_id", nullable = false)
    private RestockBean restock;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId") // 對應到 RestockDetailsId 中的 productId
    @JoinColumn(name = "product_id", nullable = false)
    private ProductBean product;

    @Column(name = "number_of_restock")
    private int numberOfRestock;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "restock_price")
    private int productPrice;

    @Column(name = "restock_total_price")
    private int restockTotalPrice;

    @Column(name = "production_date")
    private LocalDate productionDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "restock_date")
    private LocalDate restockDate;

    // Constructors
    public RestockDetailsBean() {}

    public RestockDetailsBean(RestockDetailsId id, RestockBean restock, ProductBean product) {
        this.id = id;
        this.restock = restock;
        this.product = product;
    }

    // Getters 和 Setters
    public RestockDetailsId getId() {
        return id;
    }

    public void setId(RestockDetailsId id) {
        this.id = id;
    }

    public RestockBean getRestock() {
        return restock;
    }

    public void setRestock(RestockBean restock) {
        this.restock = restock;
    }

    public ProductBean getProduct() {
        return product;
    }

    public void setProduct(ProductBean product) {
        this.product = product;
    }

    public int getNumberOfRestock() {
        return numberOfRestock;
    }

    public void setNumberOfRestock(int numberOfRestock) {
        this.numberOfRestock = numberOfRestock;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public int getRestockTotalPrice() {
        return restockTotalPrice;
    }

    public void setRestockTotalPrice(int restockTotalPrice) {
        this.restockTotalPrice = restockTotalPrice;
    }

    public LocalDate getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(LocalDate productionDate) {
        this.productionDate = productionDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getRestockDate() {
        return restockDate;
    }

    public void setRestockDate(LocalDate restockDate) {
        this.restockDate = restockDate;
    }

    @Override
    public String toString() {
        return "RestockDetailsBean{" +
                "id=" + id +
                ", productId='" + (product != null ? product.getProductId() : "null") + '\'' +
                ", productName='" + productName + '\'' +
                ", numberOfRestock=" + numberOfRestock +
                ", productPrice=" + productPrice +
                ", restockTotalPrice=" + restockTotalPrice +
                ", productionDate=" + productionDate +
                ", dueDate=" + dueDate +
                ", restockDate=" + restockDate +
                '}';
    }
}
