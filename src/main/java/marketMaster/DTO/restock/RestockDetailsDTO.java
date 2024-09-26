package marketMaster.DTO.restock;
import java.time.LocalDate;

public class RestockDetailsDTO {
    private String productId;
    private int numberOfRestock;
    private String productName;
    private int productPrice;
    private int restockTotalPrice;
    private LocalDate productionDate;
    private LocalDate restockDate;
    private LocalDate dueDate;

    // Getters and Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    public LocalDate getRestockDate() {
        return restockDate;
    }

    public void setRestockDate(LocalDate restockDate) {
        this.restockDate = restockDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}