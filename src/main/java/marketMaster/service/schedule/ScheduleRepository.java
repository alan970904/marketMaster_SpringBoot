package marketMaster.service.schedule;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import marketMaster.bean.schedule.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    List<Schedule> findByStartTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    void deleteByStartTimeAndEndTime(LocalDateTime startTime, LocalDateTime endTime);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Schedule s WHERE s.scheduleId IN :scheduleIds")
    void deleteByScheduleId(List<Integer> scheduleIds);
}