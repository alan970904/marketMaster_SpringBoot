package marketMaster.service.askForLeave;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import marketMaster.bean.askForLeave.LeaveRecordBean;

public interface LeaveRecordRepository extends JpaRepository<LeaveRecordBean, Integer> {
	
	Optional<LeaveRecordBean> findByEmpBean_EmployeeIdAndLeaveCategory_CategoryIdAndExpirationDateBetween(
	        String employeeId, Integer categoryId, LocalDate startDate, LocalDate endDate);


	
	List<LeaveRecordBean> findByEmpBean_EmployeeIdAndExpirationDateBetween(
            String employeeId, LocalDate startDate, LocalDate endDate);


}
