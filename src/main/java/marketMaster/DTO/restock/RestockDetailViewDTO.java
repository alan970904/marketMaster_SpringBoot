package marketMaster.DTO.restock;

import java.time.LocalDate;

public class RestockDetailViewDTO {
    private String restockId;
    private String employeeId;
    private String productId;
    private String productName;
    private String productCategory;
    private Integer numberOfRestock;
    private Integer productPrice;
    private LocalDate restockDate;
    private LocalDate productionDate;
    private LocalDate dueDate;

    // Constructors
    public RestockDetailViewDTO() {
    }

    public RestockDetailViewDTO(String restockId, String employeeId, String productId,
                                 String productName, String productCategory,
                                 Integer numberOfRestock, Integer productPrice,
                                 LocalDate restockDate, LocalDate productionDate, LocalDate dueDate) {
        this.restockId = restockId;
        this.employeeId = employeeId;
        this.productId = productId;
        this.productName = productName;
        this.productCategory = productCategory;
        this.numberOfRestock = numberOfRestock;
        this.productPrice = productPrice;
        this.restockDate = restockDate;
        this.productionDate = productionDate;
        this.dueDate = dueDate;
    }

    // Getters and Setters
    public String getRestockId() {
        return restockId;
    }

    public void setRestockId(String restockId) {
        this.restockId = restockId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public Integer getNumberOfRestock() {
        return numberOfRestock;
    }

    public void setNumberOfRestock(Integer numberOfRestock) {
        this.numberOfRestock = numberOfRestock;
    }

    public Integer getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Integer productPrice) {
        this.productPrice = productPrice;
    }

    public LocalDate getRestockDate() {
        return restockDate;
    }

    public void setRestockDate(LocalDate restockDate) {
        this.restockDate = restockDate;
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

    @Override
    public String toString() {
        return "RestockDetailViewBean{" +
                "restockId='" + restockId + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", productCategory='" + productCategory + '\'' +
                ", numberOfRestock=" + numberOfRestock +
                ", productPrice=" + productPrice +
                ", restockDate=" + restockDate +
                ", productionDate=" + productionDate +
                ", dueDate=" + dueDate +
                '}';
    }
}