package marketMaster.DTO.bonus;

import java.time.LocalDate;
import java.util.Objects;

public class PointsHistoryDTO {
    private int pointsHistoryId;
    private String customerTel;
    private String checkoutId;
    private String exchangeId;
    private int pointsChange;
    private LocalDate transactionDate;
    private String transactionType;
    private String encryptedCustomerId;
    private String customerName;

    public PointsHistoryDTO() {}

    public PointsHistoryDTO(int pointsHistoryId, String customerTel, String checkoutId, String exchangeId,
            int pointsChange, LocalDate transactionDate, String transactionType,
            String encryptedCustomerId, String customerName) {
	this.pointsHistoryId = pointsHistoryId;
	this.customerTel = customerTel;
	this.checkoutId = checkoutId;
	this.exchangeId = exchangeId;
	this.pointsChange = pointsChange;
	this.transactionDate = transactionDate;
	this.transactionType = transactionType;
	this.encryptedCustomerId = encryptedCustomerId;
	this.customerName = customerName;
	}

    // Getters and Setters
    public int getPointsHistoryId() { return pointsHistoryId; }
    public void setPointsHistoryId(int pointsHistoryId) { this.pointsHistoryId = pointsHistoryId; }

    public String getCustomerTel() { return customerTel; }
    public void setCustomerTel(String customerTel) { this.customerTel = customerTel; }

    public String getCheckoutId() { return checkoutId; }
    public void setCheckoutId(String checkoutId) { this.checkoutId = checkoutId; }

    public String getExchangeId() { return exchangeId; }
    public void setExchangeId(String exchangeId) { this.exchangeId = exchangeId; }

    public int getPointsChange() { return pointsChange; }
    public void setPointsChange(int pointsChange) { this.pointsChange = pointsChange; }

    public LocalDate getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    
    public String getEncryptedCustomerId() { return encryptedCustomerId; }
    public void setEncryptedCustomerId(String encryptedCustomerId) { this.encryptedCustomerId = encryptedCustomerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    @Override
    public String toString() {
    	 return "PointsHistoryDTO{" +
                 "pointsHistoryId=" + pointsHistoryId +
                 ", customerTel='" + customerTel + '\'' +
                 ", checkoutId='" + checkoutId + '\'' +
                 ", exchangeId='" + exchangeId + '\'' +
                 ", pointsChange=" + pointsChange +
                 ", transactionDate=" + transactionDate +
                 ", transactionType='" + transactionType + '\'' +
                 ", encryptedCustomerId='" + encryptedCustomerId + '\'' +
                 ", customerName='" + customerName + '\'' +
                 '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointsHistoryDTO that = (PointsHistoryDTO) o;
        return pointsHistoryId == that.pointsHistoryId &&
               pointsChange == that.pointsChange &&
               Objects.equals(customerTel, that.customerTel) &&
               Objects.equals(checkoutId, that.checkoutId) &&
               Objects.equals(exchangeId, that.exchangeId) &&
               Objects.equals(transactionDate, that.transactionDate) &&
               Objects.equals(transactionType, that.transactionType) &&
               Objects.equals(encryptedCustomerId, that.encryptedCustomerId) &&
               Objects.equals(customerName, that.customerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pointsHistoryId, customerTel, checkoutId, exchangeId, pointsChange, 
                            transactionDate, transactionType, encryptedCustomerId, customerName);
    }
}