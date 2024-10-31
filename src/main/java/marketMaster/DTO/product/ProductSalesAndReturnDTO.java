package marketMaster.DTO.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSalesAndReturnDTO {
    private String productId;              // 商品編號
    private String productName;            // 商品名稱
    private String productCategory;        // 商品類別
    private long salesQuantity;            // 銷售數量
    private long salesAmount;              // 銷售金額
    private double returnRate;             // 退貨率(%)
    private int returnQuantity;            // 退貨數量
    private double totalRevenue;           // 總營收(扣除退貨)

    // 額外的計算方法
    public double getReturnRateFormatted() {
        return Math.round(returnRate * 100.0) / 100.0;  // 將退貨率格式化為兩位小數
    }

    public double getEffectiveSalesRate() {
        return 100 - returnRate;  // 有效銷售率
    }

    public double getAveragePrice() {
        return salesQuantity > 0 ? (double) salesAmount / salesQuantity : 0;  // 平均售價
    }

    public double getReturnLossAmount() {
        return returnQuantity * getAveragePrice();  // 退貨損失金額
    }

    public String getReturnRateStatus() {
        if (returnRate < 5) {
            return "正常";
        } else if (returnRate < 10) {
            return "偏高";
        } else {
            return "異常";
        }
    }

    // 用於圖表顯示的輔助方法
    public String getFormattedSalesAmount() {
        return String.format("%,d", salesAmount);  // 格式化銷售金額，加上千位分隔符
    }

    public String getFormattedReturnRate() {
        return String.format("%.2f%%", returnRate);  // 格式化退貨率，顯示為百分比
    }

    public String getFormattedTotalRevenue() {
        return String.format("%,d", Math.round(totalRevenue));  // 格式化總營收
    }

    // 建構子
    public ProductSalesAndReturnDTO(String productId, String productName, String productCategory,
                                  long salesQuantity, long salesAmount, double returnRate) {
        this.productId = productId;
        this.productName = productName;
        this.productCategory = productCategory;
        this.salesQuantity = salesQuantity;
        this.salesAmount = salesAmount;
        this.returnRate = returnRate;
        this.returnQuantity = Math.round((float)(salesQuantity * returnRate / 100));
        this.totalRevenue = salesAmount - getReturnLossAmount();
    }

    // 用於比較的方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductSalesAndReturnDTO that = (ProductSalesAndReturnDTO) o;
        return productId != null ? productId.equals(that.productId) : that.productId == null;
    }

    @Override
    public int hashCode() {
        return productId != null ? productId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ProductSalesAndReturnDTO{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", productCategory='" + productCategory + '\'' +
                ", salesQuantity=" + salesQuantity +
                ", salesAmount=" + salesAmount +
                ", returnRate=" + getFormattedReturnRate() +
                ", returnQuantity=" + returnQuantity +
                ", totalRevenue=" + getFormattedTotalRevenue() +
                '}';
    }
}