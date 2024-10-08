package marketMaster.DTO.bonus;

import java.time.LocalDate;
import java.util.Objects;

public class BonusExchangeDTO {
    private String exchangeId;
    private String customerTel;
    private String productId;
    private String productName;
    private int usePoints;
    private int numberOfExchange;
    private LocalDate exchangeDate;

    public BonusExchangeDTO() {}

    public BonusExchangeDTO(String exchangeId, String customerTel, String productId, String productName, 
                            int usePoints, int numberOfExchange, LocalDate exchangeDate) {
        this.exchangeId = exchangeId;
        this.customerTel = customerTel;
        this.productId = productId;
        this.productName = productName;
        this.usePoints = usePoints;
        this.numberOfExchange = numberOfExchange;
        this.exchangeDate = exchangeDate;
    }

    public String getExchangeId() { return exchangeId; }
    public void setExchangeId(String exchangeId) { this.exchangeId = exchangeId; }

    public String getCustomerTel() { return customerTel; }
    public void setCustomerTel(String customerTel) { this.customerTel = customerTel; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getUsePoints() { return usePoints; }
    public void setUsePoints(int usePoints) { this.usePoints = usePoints; }

    public int getNumberOfExchange() { return numberOfExchange; }
    public void setNumberOfExchange(int numberOfExchange) { this.numberOfExchange = numberOfExchange; }

    public LocalDate getExchangeDate() { return exchangeDate; }
    public void setExchangeDate(LocalDate exchangeDate) { this.exchangeDate = exchangeDate; }

    @Override
    public String toString() {
        return "BonusExchangeDTO{" +
               "exchangeId='" + exchangeId + '\'' +
               ", customerTel='" + customerTel + '\'' +
               ", productId='" + productId + '\'' +
               ", productName='" + productName + '\'' +
               ", usePoints=" + usePoints +
               ", numberOfExchange=" + numberOfExchange +
               ", exchangeDate=" + exchangeDate +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BonusExchangeDTO that = (BonusExchangeDTO) o;
        return usePoints == that.usePoints &&
               numberOfExchange == that.numberOfExchange &&
               Objects.equals(exchangeId, that.exchangeId) &&
               Objects.equals(customerTel, that.customerTel) &&
               Objects.equals(productId, that.productId) &&
               Objects.equals(productName, that.productName) &&
               Objects.equals(exchangeDate, that.exchangeDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exchangeId, customerTel, productId, productName, usePoints, numberOfExchange, exchangeDate);
    }
}