package marketMaster.service.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import marketMaster.bean.schedule.Schedule;
import marketMaster.service.employee.EmployeeRepository;
import marketMaster.bean.employee.EmpBean;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

	@Autowired
	private ScheduleRepository scheduleRepo;

	@Autowired
	private EmployeeRepository empRepo;

	public Map<Object, Map<Object, List<Map<String, Object>>>> getSchedulesByYearAndMonth(int year, int month) {
		LocalDateTime startDateTime = LocalDateTime.of(year, month, 1, 0, 0);
		LocalDateTime endDateTime = startDateTime.plusMonths(1).minusSeconds(1);

		List<Schedule> schedules = scheduleRepo.findByStartTimeBetween(startDateTime, endDateTime);

		return schedules.stream().collect(Collectors.groupingBy(schedule -> schedule.getStartTime().getDayOfMonth(),
				Collectors.groupingBy(
						schedule -> schedule.getStartTime().toLocalTime() + " - " + schedule.getEndTime().toLocalTime(),
						Collectors
								.mapping(
										schedule -> Map.of("scheduleId", schedule.getScheduleId(), "employeeId",
												schedule.getEmpBean().getEmployeeId(), "employeeName",
												schedule.getEmpBean().getEmployeeName(), "startTime",
												schedule.getStartTime(), "endTime", schedule.getEndTime()),
										Collectors.toList()))));
	}

	public void updateSchedule(List<Integer> scheduleIds, int year, int month, int day, String time,
			List<String> employeeIds) {

		try {
			String[] timeParts = time.split(" - ");
			LocalDateTime startDateTime = LocalDateTime.of(year, month, day, LocalTime.parse(timeParts[0]).getHour(),
					LocalTime.parse(timeParts[0]).getMinute());
			LocalDateTime endDateTime = LocalDateTime.of(year, month, day, LocalTime.parse(timeParts[1]).getHour(),
					LocalTime.parse(timeParts[1]).getMinute());

			System.out.println("scheduleId:" + scheduleIds);
			scheduleRepo.deleteByScheduleId(scheduleIds);

			for (String employeeId : employeeIds) {
				EmpBean employee = empRepo.findById(employeeId)
						.orElseThrow(() -> new RuntimeException("員工不存在，ID: " + employeeId));
				Schedule schedule = new Schedule();

				schedule.setEmpBean(employee);
				schedule.setStartTime(startDateTime);
				schedule.setEndTime(endDateTime);

				scheduleRepo.save(schedule);
			}
		} catch (Exception e) {
			throw new RuntimeException("更新排程時發生錯誤：" + e.getMessage(), e);
		}
	}

	public void addSchedules(int year, int month, int day, String time, List<String> employeeIds) {
		try {
			String[] timeParts = time.split(" - ");
			LocalDateTime startDateTime = LocalDateTime.of(year, month, day, LocalTime.parse(timeParts[0]).getHour(),
					LocalTime.parse(timeParts[0]).getMinute());
			LocalDateTime endDateTime = LocalDateTime.of(year, month, day, LocalTime.parse(timeParts[1]).getHour(),
					LocalTime.parse(timeParts[1]).getMinute());

			for (String employeeId : employeeIds) {
				EmpBean employee = empRepo.findById(employeeId)
						.orElseThrow(() -> new RuntimeException("員工不存在，ID: " + employeeId));
				Schedule newSchedule = new Schedule();

				newSchedule.setEmpBean(employee);
				newSchedule.setStartTime(startDateTime);
				newSchedule.setEndTime(endDateTime);

				scheduleRepo.save(newSchedule);
			}
		} catch (Exception e) {
			throw new RuntimeException("新增排程時發生錯誤：" + e.getMessage(), e);
		}
	}

	public void deleteSchedule(int year, int month, int day, String time) {
		String[] timeParts = time.split(" - ");
		LocalDateTime startDateTime = LocalDateTime.of(year, month, day, LocalTime.parse(timeParts[0]).getHour(),
				LocalTime.parse(timeParts[0]).getMinute());
		LocalDateTime endDateTime = LocalDateTime.of(year, month, day, LocalTime.parse(timeParts[1]).getHour(),
				LocalTime.parse(timeParts[1]).getMinute());

		scheduleRepo.deleteByStartTimeAndEndTime(startDateTime, endDateTime);
	}

	public Schedule findById(Integer scheduleId) {
		return scheduleRepo.findById(scheduleId).orElseThrow(() -> new RuntimeException("排程不存在"));
	}

	 @Transactional
	    public void deleteSchedules(List<Integer> scheduleIds) {
	        scheduleRepo.deleteAllById(scheduleIds);
	    }
}