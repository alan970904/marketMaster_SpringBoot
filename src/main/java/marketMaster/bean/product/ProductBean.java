package marketMaster.bean.product;

import jakarta.persistence.*;
import java.io.Serializable;

import org.springframework.stereotype.Component;


@Entity
@Table(name = "products")
@Component
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

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public String getProductName() {
        return productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public int getproductSafeInventory() {
        return productSafeInventory;
    }

    public int getNumberOfShelve() {
        return numberOfShelve;
    }

    public int getNumberOfInventory() {
        return numberOfInventory;
    }

    public int getNumberOfSale() {
        return numberOfSale;
    }

    public int getNumberOfExchange() {
        return numberOfExchange;
    }

    public int getNumberOfDestruction() {
        return numberOfDestruction;
    }

    public int getNumberOfRemove() {
        return numberOfRemove;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public void setproductSafeInventory(int productSafeInventory) {
        this.productSafeInventory = productSafeInventory;
    }

    public void setNumberOfShelve(int numberOfShelve) {
        this.numberOfShelve = numberOfShelve;
    }

    public void setNumberOfInventory(int numberOfInventory) {
        this.numberOfInventory = numberOfInventory;
    }

    public void setNumberOfSale(int numberOfSale) {
        this.numberOfSale = numberOfSale;
    }

    public void setNumberOfExchange(int numberOfExchange) {
        this.numberOfExchange = numberOfExchange;
    }

    public void setNumberOfDestruction(int numberOfDestruction) {
        this.numberOfDestruction = numberOfDestruction;
    }

    public void setNumberOfRemove(int numberOfRemove) {
        this.numberOfRemove = numberOfRemove;
    }

    public ProductBean(String productId, String productCategory, String productName, int productPrice,
                       int productSafeInventory, int numberOfShelve, int numberOfInventory, int numberOfSale, 
                       int numberOfExchange, int numberOfDestruction, int numberOfRemove) {
        super();
        this.productId = productId;
        this.productCategory = productCategory;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productSafeInventory = productSafeInventory;
        this.numberOfShelve = numberOfShelve;
        this.numberOfInventory = numberOfInventory;
        this.numberOfSale = numberOfSale;
        this.numberOfExchange = numberOfExchange;
        this.numberOfDestruction = numberOfDestruction;
        this.numberOfRemove = numberOfRemove;
    }

    public ProductBean(String productId, int numberOfShelve, int numberOfInventory) {
        super();
        this.productId = productId;
        this.numberOfShelve = numberOfShelve;
        this.numberOfInventory = numberOfInventory;
    }
    
    public ProductBean(String productId, String productName, String productCategory, int productPrice) {
        this.productId = productId;
        this.productName = productName;
        this.productCategory = productCategory;
        this.productPrice = productPrice;
    }

    public ProductBean(String productName) {
        super();
        this.productName = productName;
    }

    public ProductBean() {
        super();
    }
}