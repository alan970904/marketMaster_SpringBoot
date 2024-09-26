package marketMaster.DTO.restock;

import java.time.LocalDate;
import java.util.List;

public class RestockDTO {
    private String restockId;
    private String employeeId;
    private int restockTotalPrice;
    private LocalDate restockDate;
    private List<RestockDetailsDTO> restockDetails;

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

    public int getRestockTotalPrice() {
        return restockTotalPrice;
    }

    public void setRestockTotalPrice(int restockTotalPrice) {
        this.restockTotalPrice = restockTotalPrice;
    }

    public LocalDate getRestockDate() {
        return restockDate;
    }

    public void setRestockDate(LocalDate restockDate) {
        this.restockDate = restockDate;
    }

    public List<RestockDetailsDTO> getRestockDetails() {
        return restockDetails;
    }

    public void setRestockDetails(List<RestockDetailsDTO> restockDetails) {
        this.restockDetails = restockDetails;
    }
}
