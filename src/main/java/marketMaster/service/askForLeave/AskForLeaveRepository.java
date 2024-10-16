package marketMaster.service.askForLeave;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import marketMaster.bean.askForLeave.AskForLeave;

public interface AskForLeaveRepository extends JpaRepository<AskForLeave, String> {

	@Query("SELECT MAX(a.leaveId) FROM AskForLeave a")
	String findMaxLeaveId();

	List<AskForLeave> findByEmpBeanEmployeeId(String employeeId);

	Page<AskForLeave> findByEmpBeanEmployeeId(String employeeId, Pageable pageable);

	boolean existsByEmpBeanEmployeeIdAndStarTime(String employeeId, LocalDateTime startTime);

	@Query("SELECT a FROM AskForLeave a JOIN a.empBean e JOIN e.rankLevel r WHERE r.limitsOfAuthority < :limits")
	Page<AskForLeave> findByRankLimitsOfAuthorityLessThan(@Param("limits") int limits, Pageable pageable);

	@Query("SELECT a FROM AskForLeave a JOIN a.empBean e JOIN e.rankLevel r WHERE r.limitsOfAuthority <= :limits")
	Page<AskForLeave> findByRankLimitsOfAuthorityLessThanEqual(@Param("limits") int limits, Pageable pageable);

}
