package marketMaster.service.askForLeave;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import marketMaster.bean.askForLeave.AskForLeave;

public interface AskForLeaveRepository extends JpaRepository<AskForLeave, String> {

	@Query("SELECT MAX(a.leaveId) FROM AskForLeave a")
	String findMaxLeaveId();
	
	List<AskForLeave> findByEmpBeanEmployeeId(String employeeId);

}
