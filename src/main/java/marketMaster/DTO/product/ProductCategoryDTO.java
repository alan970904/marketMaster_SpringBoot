package marketMaster.DTO.product;

public class ProductCategoryDTO {
    private String productCategory;

    public ProductCategoryDTO() {
    }

    public ProductCategoryDTO( String productCategory) {
        this.productCategory = productCategory;
    }



    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    @Override
    public String toString() {
        return "ProductCategoryDTO{" +
                ", productCategory='" + productCategory + '\'' +
                '}';
    }
}
