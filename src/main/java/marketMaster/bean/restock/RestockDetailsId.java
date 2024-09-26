package marketMaster.bean.restock;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RestockDetailsId implements Serializable {

    private String restockId;
    private String productId;

    public RestockDetailsId() {}

    public RestockDetailsId(String restockId, String productId) {
        this.restockId = restockId;
        this.productId = productId;
    }

    // Getters 和 Setters
    public String getRestockId() {
        return restockId;
    }

    public void setRestockId(String restockId) {
        this.restockId = restockId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    // Override equals 和 hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RestockDetailsId that = (RestockDetailsId) o;

        if (!Objects.equals(restockId, that.restockId)) return false;
        return Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        int result = restockId != null ? restockId.hashCode() : 0;
        result = 31 * result + (productId != null ? productId.hashCode() : 0);
        return result;
    }
}
