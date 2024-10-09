package marketMaster.DTO.bonus;

import java.util.Objects;

public class CustomerPointsDTO {
    private String customerTel;
    private String customerName;
    private int totalPoints;

    public CustomerPointsDTO() {}

    public CustomerPointsDTO(String customerTel, String customerName, int totalPoints) {
        this.customerTel = customerTel;
        this.customerName = customerName;
        this.totalPoints = totalPoints;
    }

    @Override
    public String toString() {
        return "CustomerPointsDTO{" +
               "customerTel='" + customerTel + '\'' +
               ", customerName='" + customerName + '\'' +
               ", totalPoints=" + totalPoints +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerPointsDTO that = (CustomerPointsDTO) o;
        return totalPoints == that.totalPoints &&
               Objects.equals(customerTel, that.customerTel) &&
               Objects.equals(customerName, that.customerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerTel, customerName, totalPoints);
    }
}