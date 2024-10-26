package marketMaster.service.schedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import marketMaster.bean.schedule.ScheduleBean;

public interface ScheduleRepository extends JpaRepository<ScheduleBean, Integer> {

	@Modifying
	void deleteByStartTimeAndEndTime(LocalTime startTime, LocalTime endTime);

	@Modifying
	@Transactional
	@Query("DELETE FROM ScheduleBean s WHERE s.scheduleId IN :scheduleIds")
	void deleteByScheduleId(List<Integer> scheduleIds);

	List<ScheduleBean> findByScheduleDateBetween(LocalDate startDate, LocalDate endDate);
	
	List<ScheduleBean> findByEmpBean_employeeIdAndScheduleDateBetween(String employeeId,LocalDate startDate, LocalDate endDate);

	@Query(value = "SELECT * FROM schedule s WHERE s.schedule_date = :scheduleDate "
			+ "AND s.start_time = :startTime AND s.end_time = :endTime", nativeQuery = true)
	List<ScheduleBean> findByScheduleDateAndTimeNative(@Param("scheduleDate") String scheduleDate,
			@Param("startTime") String startTime, @Param("endTime") String endTime);

	@Query("SELECT s FROM ScheduleBean s WHERE s.empBean.employeeId = :employeeId AND " +
		       "CAST(CONCAT(s.scheduleDate, ' ', s.startTime) AS LocalDateTime) >= :startDateTime AND " +
		       "CAST(CONCAT(s.scheduleDate, ' ', s.endTime) AS LocalDateTime) <= :endDateTime")
		List<ScheduleBean> findByEmployeeIdAndDateTimeRange(
		    @Param("employeeId") String employeeId,
		    @Param("startDateTime") LocalDateTime startDateTime,
		    @Param("endDateTime") LocalDateTime endDateTime);

}