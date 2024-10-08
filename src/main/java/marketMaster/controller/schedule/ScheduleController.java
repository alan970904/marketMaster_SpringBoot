package marketMaster.controller.schedule;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import marketMaster.bean.employee.EmpBean;
import marketMaster.service.employee.EmployeeService;
import marketMaster.service.schedule.ScheduleService;

@Controller
@RequestMapping("/schedules")
public class ScheduleController {

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private EmployeeService empService;

	@GetMapping("/view")
	public String viewSchedules() {
		return "schedule/view";
	}

	@GetMapping("/employees/all")
	@ResponseBody
	public ResponseEntity<?> getAllEmployees() {
		try {
			List<EmpBean> employees = empService.findAllEmp();
			List<Map<String, String>> employeeList = employees.stream()
					.map(emp -> Map.of("id", emp.getEmployeeId(), "name", emp.getEmployeeName()))
					.collect(Collectors.toList());
			return ResponseEntity.ok(employeeList);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("獲取員工資料失敗：" + e.getMessage());
		}
	}

	@GetMapping("/oneMonth")
	public String getMonthlySchedules(@RequestParam int year, @RequestParam int month, Model model) {
		Map<Object, Map<Object, List<Map<String, Object>>>> schedulesByDayAndTime = scheduleService
				.getSchedulesByYearAndMonth(year, month);

		LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
		int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;
		int daysInMonth = firstDayOfMonth.lengthOfMonth();

		model.addAttribute("schedulesByDayAndTime", schedulesByDayAndTime);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("firstDayOfWeek", firstDayOfWeek);
		model.addAttribute("daysInMonth", daysInMonth);

		return "schedule/oneMonth";
	}

	@PostMapping("/update")
	@ResponseBody
	public ResponseEntity<?> updateSchedule(@RequestParam String scheduleId, @RequestParam int year,
			@RequestParam int month, @RequestParam int day, @RequestParam String time,
			@RequestParam List<String> employeeIds) {
		try {
			List<Integer> scheduleIds = Arrays.stream(scheduleId.split(",")).map(Integer::parseInt)
					.collect(Collectors.toList());

			System.out.println("Con scheduleIds: " + scheduleIds);

			scheduleService.updateSchedule(scheduleIds, year, month, day, time, employeeIds);

			return ResponseEntity.ok().build();
		} catch (NumberFormatException e) {
			return ResponseEntity.badRequest().body("Invalid schedule ID format: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/add")
	@ResponseBody
	public String addSchedule(@RequestParam int year, @RequestParam int month, @RequestParam int day,
			@RequestParam String time, @RequestParam List<String> employeeIds) {
		scheduleService.addSchedules(year, month, day, time, employeeIds);
		return "排班新增成功";
	}

	@PostMapping("/delete")
	@ResponseBody
	public ResponseEntity<?> deleteSchedules(@RequestParam List<String> scheduleIds) {
	    try {
	        List<Integer> intScheduleIds = scheduleIds.stream().map(Integer::parseInt).collect(Collectors.toList());
	        scheduleService.deleteSchedules(intScheduleIds);
	        return ResponseEntity.ok().body("排班刪除成功");
	    } catch (NumberFormatException e) {
	        return ResponseEntity.badRequest().body("無效的排班 ID 格式");
	    }
	}
}