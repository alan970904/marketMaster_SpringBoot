package marketMaster.bean.product;

import jakarta.persistence.*;
import marketMaster.bean.restock.RestockDetailsBean;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "products")
public class ProductBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(name = "product_category")
    private String productCategory;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_price")
    private int productPrice;

    @Column(name = "product_safeinventory")
    private int productSafeInventory;

    @Column(name = "number_of_shelve")
    private int numberOfShelve;

    @Column(name = "number_of_inventory")
    private int numberOfInventory;

    @Column(name = "number_of_sale")
    private int numberOfSale;

    @Column(name = "number_of_exchange")
    private int numberOfExchange;

    @Column(name = "number_of_destruction")
    private int numberOfDestruction;

    @Column(name = "number_of_remove")
    private int numberOfRemove;

    @OneToMany(mappedBy = "product")
    private List<RestockDetailsBean> restockDetails;

    // Constructors
    public ProductBean() {
        super();
    }

    // 其他建構函數略

    // Getters 和 Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    // 其他 getters 和 setters 略
}
