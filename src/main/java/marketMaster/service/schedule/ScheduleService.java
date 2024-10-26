package marketMaster.service.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import marketMaster.bean.schedule.ScheduleBean;
import marketMaster.service.employee.EmployeeRepository;
import marketMaster.bean.employee.EmpBean;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleService {

	@Autowired
	private ScheduleRepository scheduleRepo;

	@Autowired
	private EmployeeRepository empRepo;

	public List<ScheduleBean> findAllSchedules() {
		return scheduleRepo.findAll();
	}

	public Map<Integer, Map<String, List<Map<String, Object>>>> getSchedulesByYearAndMonth(int year, int month) {
		LocalDate startDate = LocalDate.of(year, month, 1);
		LocalDate endDate = startDate.plusMonths(1).minusDays(1);
		List<ScheduleBean> schedules = scheduleRepo.findByScheduleDateBetween(startDate, endDate);

		Map<Integer, Map<String, List<Map<String, Object>>>> groupedSchedules = new HashMap<>();

		for (ScheduleBean schedule : schedules) {
			int dayOfMonth = schedule.getScheduleDate().getDayOfMonth();
			String shiftTime = schedule.getStartTime() + " - " + schedule.getEndTime();

			Map<String, Object> scheduleDetails = new HashMap<>();
			scheduleDetails.put("scheduleId", schedule.getScheduleId());
			scheduleDetails.put("employeeId", schedule.getEmpBean().getEmployeeId());
			scheduleDetails.put("employeeName", schedule.getEmpBean().getEmployeeName());
			scheduleDetails.put("startTime", schedule.getScheduleDate() + " " + schedule.getStartTime());
			scheduleDetails.put("endTime", schedule.getScheduleDate() + " " + schedule.getEndTime());
			scheduleDetails.put("scheduleHour", schedule.getScheduleHour());

			groupedSchedules.putIfAbsent(dayOfMonth, new HashMap<>());
			groupedSchedules.get(dayOfMonth).putIfAbsent(shiftTime, new ArrayList<>());
			groupedSchedules.get(dayOfMonth).get(shiftTime).add(scheduleDetails);
		}

		return groupedSchedules;
	}
	
	public Map<Integer, Map<String, List<Map<String, Object>>>> getMiniSchedules(String employeeId, int year, int month) {
		LocalDate startDate = LocalDate.of(year, month, 1);
		LocalDate endDate = startDate.plusMonths(1).minusDays(1);
		List<ScheduleBean> schedules = scheduleRepo.findByEmpBean_employeeIdAndScheduleDateBetween(employeeId,startDate, endDate);

		Map<Integer, Map<String, List<Map<String, Object>>>> groupedSchedules = new HashMap<>();

		for (ScheduleBean schedule : schedules) {
			int dayOfMonth = schedule.getScheduleDate().getDayOfMonth();

			Map<String, Object> scheduleDetails = new HashMap<>();
			scheduleDetails.put("scheduleId", schedule.getScheduleId());
			scheduleDetails.put("employeeId", employeeId);
			scheduleDetails.put("employeeName", schedule.getEmpBean().getEmployeeName());

			groupedSchedules.putIfAbsent(dayOfMonth, new HashMap<>());
		}

		return groupedSchedules;
	}

	// 計算下一個月的年份和月份
	public Map<String, Integer> getNextMonth(Integer year, Integer month) {
		LocalDate date = LocalDate.of(year, month, 1).plusMonths(1);
		return Map.of("year", date.getYear(), "month", date.getMonthValue());
	}

	// 計算上一個月的年份和月份
	public Map<String, Integer> getPreviousMonth(Integer year, Integer month) {
		LocalDate date = LocalDate.of(year, month, 1).minusMonths(1);
		return Map.of("year", date.getYear(), "month", date.getMonthValue());
	}

	// 更新排班
	@Transactional
	public void updateSchedule(List<Integer> scheduleIds, int year, int month, int day, String startTime,
			String endTime, List<String> employeeIds) {

		try {
			LocalTime start = LocalTime.parse(startTime);
			LocalTime end = LocalTime.parse(endTime);

			int hours = (int) Duration.between(start, end).toHours();
			int minutes = (int) Duration.between(start, end).toMinutes() % 60;

			if (minutes > 0) {
				hours++;
			}

			scheduleRepo.deleteByScheduleId(scheduleIds);

			for (String employeeId : employeeIds) {
				EmpBean employee = empRepo.findById(employeeId)
						.orElseThrow(() -> new RuntimeException("員工不存在，ID: " + employeeId));

				ScheduleBean schedule = new ScheduleBean();
				schedule.setEmpBean(employee);
				schedule.setScheduleDate(LocalDate.of(year, month, day));
				schedule.setStartTime(start);
				schedule.setEndTime(end);
				schedule.setScheduleHour(hours);

				scheduleRepo.save(schedule);
			}

		} catch (Exception e) {
			throw new RuntimeException("更新排程時發生錯誤：" + e.getMessage(), e);
		}
	}

	// 新增排班
	public void addSchedules(int year, int month, int day, String startTime, String endTime, List<String> employeeIds) {
		try {
			LocalTime start = LocalTime.parse(startTime);
			LocalTime end = LocalTime.parse(endTime);

			// 計算時間差
			int hours = (int) Duration.between(start, end).toHours(); // 獲得整小時
			int minutes = (int) Duration.between(start, end).toMinutes() % 60; // 獲得分鐘

			if (minutes > 0) {
				hours++;
			}

			for (String employeeId : employeeIds) {
				EmpBean employee = empRepo.findById(employeeId)
						.orElseThrow(() -> new RuntimeException("員工不存在，ID: " + employeeId));

				ScheduleBean newSchedule = new ScheduleBean();
				newSchedule.setEmpBean(employee);
				newSchedule.setScheduleDate(LocalDate.of(year, month, day));
				newSchedule.setStartTime(start);
				newSchedule.setEndTime(end);
				newSchedule.setScheduleHour(hours);

				scheduleRepo.save(newSchedule);
			}
		} catch (Exception e) {
			throw new RuntimeException("新增排程時發生錯誤：" + e.getMessage(), e);
		}
	}

	// 刪除排班
	public void deleteSchedule(int year, int month, int day, String time) {
		String[] timeParts = time.split(" - ");
		LocalTime startTime = LocalTime.parse(timeParts[0]);
		LocalTime endTime = LocalTime.parse(timeParts[1]);

		scheduleRepo.deleteByStartTimeAndEndTime(startTime, endTime);
	}

	public ScheduleBean findById(Integer scheduleId) {
		return scheduleRepo.findById(scheduleId).orElseThrow(() -> new RuntimeException("排程不存在"));
	}

	@Transactional
	public void deleteSchedules(List<Integer> scheduleIds) {
		scheduleRepo.deleteAllById(scheduleIds);
	}

	public List<ScheduleBean> findSchedulesByDateAndTime(String scheduleDate, String startTime, String endTime) {

		List<ScheduleBean> result = scheduleRepo.findByScheduleDateAndTimeNative(scheduleDate, startTime, endTime);

		return result;
	}

	public Map<String, Integer> getTotalHoursByMonth(int year, int month) {
		Map<String, Integer> totalHours = new HashMap<>();

		List<ScheduleBean> schedules = scheduleRepo.findByScheduleDateBetween(LocalDate.of(year, month, 1),
				LocalDate.of(year, month, Month.of(month).length(false)));

		for (ScheduleBean schedule : schedules) {
			String employeeName = schedule.getEmpBean().getEmployeeName();
			Integer hours = schedule.getScheduleHour();
			if (hours == null) {
				hours = 0;
			}

			 totalHours.put(employeeName, totalHours.getOrDefault(employeeName, 0) + hours);
		}

		return totalHours;
	}
	
	 public List<ScheduleBean> findSchedulesByDateTimeRange(String employeeId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
	        List<ScheduleBean> schedule = scheduleRepo.findByEmployeeIdAndDateTimeRange(employeeId, startDateTime, endDateTime);
	        return schedule;
	    }

}
