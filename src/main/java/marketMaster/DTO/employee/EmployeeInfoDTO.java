package marketMaster.DTO.employee;


public class EmployeeInfoDTO {
   private String employeeId;
   private String employeeName;

    @Override
    public String toString() {
        return "EmployeeInfoDTO{" +
                "employeeId='" + employeeId + '\'' +
                ", employeeName='" + employeeName + '\'' +
                '}';
    }

    public EmployeeInfoDTO() {
    }

    public EmployeeInfoDTO(String employeeName, String employeeId) {
        this.employeeName = employeeName;
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
}
