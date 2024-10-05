package marketMaster.DTO.restock.SupplierDTO;


public class SupplierProductDTO {
    private String productId;
    private String productName;

    public SupplierProductDTO(String productId, String productName) {
        this.productId = productId;
        this.productName = productName;
    }

    // Getter 和 Setter 方法
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
}
