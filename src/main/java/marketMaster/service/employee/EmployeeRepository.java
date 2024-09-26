package marketMaster.service.employee;

import marketMaster.DTO.employee.EmployeeInfoDTO;
import marketMaster.bean.employee.EmpBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface EmployeeRepository extends JpaRepository<EmpBean,String> {

    @Query("SELECT new marketMaster.DTO.employee.EmployeeInfoDTO(e.employeeId, e.employeeName) FROM EmpBean e")
    List<EmployeeInfoDTO>findAllEmployeeInfo();
}
