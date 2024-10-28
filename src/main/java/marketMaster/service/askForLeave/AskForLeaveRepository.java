package marketMaster.service.askForLeave;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import marketMaster.bean.askForLeave.AskForLeaveBean;

public interface AskForLeaveRepository extends JpaRepository<AskForLeaveBean, String>, JpaSpecificationExecutor<AskForLeaveBean> {

	@Query("SELECT MAX(a.leaveId) FROM AskForLeaveBean a")
	String findMaxLeaveId();

	List<AskForLeaveBean> findByEmpBeanEmployeeId(String employeeId);
	
	List<AskForLeaveBean> findByEmpBeanEmployeeIdAndApprovedStatusIn(String employeeId, List<String> approvedStatus);


	Page<AskForLeaveBean> findByEmpBeanEmployeeId(String employeeId, Pageable pageable);

	boolean existsByEmpBeanEmployeeIdAndStarTime(String employeeId, LocalDateTime startTime);

	@Query("SELECT a FROM AskForLeaveBean a JOIN a.empBean e JOIN e.rankLevel r WHERE r.limitsOfAuthority < :limits")
	Page<AskForLeaveBean> findByRankLimitsOfAuthorityLessThan(@Param("limits") int limits, Pageable pageable);

	@Query("SELECT a FROM AskForLeaveBean a JOIN a.empBean e JOIN e.rankLevel r WHERE r.limitsOfAuthority <= :limits")
	Page<AskForLeaveBean> findByRankLimitsOfAuthorityLessThanEqual(@Param("limits") int limits, Pageable pageable);

}
