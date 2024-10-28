package marketMaster.service.employee;

import marketMaster.DTO.employee.EmployeeInfoDTO;
import marketMaster.bean.employee.EmpBean;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<EmpBean,String> {

	Optional<EmpBean> findByEmployeeId(String employeeId);
	
	@Query("SELECT e.isFirstLogin FROM EmpBean e WHERE e.employeeId = ?1")
	boolean isFirstLogin(String employeeId);
	
	Page<EmpBean> findByResigndateIsNull(Pageable pageable);
	
	List<EmpBean> findByResigndateIsNull();
	
	Page<EmpBean> findByEmployeeNameContainingAndResigndateIsNull(String name, Pageable pageable);
	
	Page<EmpBean> findByEmployeeNameContaining(String name, Pageable pageable);
	
	@Query("SELECT e.employeeId FROM EmpBean e ORDER BY e.employeeId DESC")
	List<String> findLastEmployeeId();
	
    int countByPositionId(String positionId);

    int countByPositionIdAndResigndateIsNull(String positionId);
    
    // Notification使用
    List<EmpBean> findByRankLevel_PositionNameIn(List<String> roles);
	
	// restock使用
    @Query("SELECT new marketMaster.DTO.employee.EmployeeInfoDTO(e.employeeId, e.employeeName) FROM EmpBean e")
    List<EmployeeInfoDTO>findAllEmployeeInfo();
}
