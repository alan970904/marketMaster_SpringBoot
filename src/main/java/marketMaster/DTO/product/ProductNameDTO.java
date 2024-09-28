package marketMaster.DTO.product;

public class ProductNameDTO {
    private String productName;

    public ProductNameDTO() {
    }

    public ProductNameDTO( String productName) {

        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String toString() {
        return "ProductNameDTO{" +
                "productName='" + productName + '\'' +
                '}';
    }

}
