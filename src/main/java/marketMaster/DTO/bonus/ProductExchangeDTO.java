package marketMaster.DTO.bonus;

import java.util.Objects;
//從商品表取得可兌換商品DTO
//此份應該已改名為ItemMgnDTO
public class ProductExchangeDTO {
    private String productId;
    private String productName;
    private String productCategory;
    private int productPrice;
    private int numberOfInventory;

    public ProductExchangeDTO() {}

    public ProductExchangeDTO(String productId, String productName, String productCategory, 
                              int productPrice, int numberOfInventory) {
        this.productId = productId;
        this.productName = productName;
        this.productCategory = productCategory;
        this.productPrice = productPrice;
        this.numberOfInventory = numberOfInventory;
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

	public int getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(int productPrice) {
		this.productPrice = productPrice;
	}
	
	public int getNumberOfInventory() {
		return numberOfInventory;
	}

	public void setNumberOfInventory(int numberOfInventory) {
		this.numberOfInventory = numberOfInventory;
	}

	@Override
    public String toString() {
        return "ProductExchangeDTO{" +
               "productId='" + productId + '\'' +
               ", productName='" + productName + '\'' +
               ", productCategory='" + productCategory + '\'' +
               ", productPrice=" + productPrice +
               ", numberOfInventory=" + numberOfInventory +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductExchangeDTO that = (ProductExchangeDTO) o;
        return productPrice == that.productPrice &&
               numberOfInventory == that.numberOfInventory &&
               Objects.equals(productId, that.productId) &&
               Objects.equals(productName, that.productName) &&
               Objects.equals(productCategory, that.productCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, productName, productCategory, productPrice, numberOfInventory);
    }
}