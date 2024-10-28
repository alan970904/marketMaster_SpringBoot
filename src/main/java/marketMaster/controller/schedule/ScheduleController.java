 package marketMaster.controller.schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import marketMaster.bean.employee.EmpBean;
import marketMaster.bean.schedule.ScheduleBean;
import marketMaster.service.employee.EmployeeServiceImpl;
import marketMaster.service.schedule.ScheduleService;

@Controller
@RequestMapping("/schedules")
public class ScheduleController {

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private EmployeeServiceImpl empService;

	@GetMapping("/viewSchedules")
	public String viewSchedules(Model model) {
		LocalDate now = LocalDate.now();
		int year = now.getYear();
		int month = now.getMonthValue();

		Map<Integer, Map<String, List<Map<String, Object>>>> schedulesByDayAndTime = scheduleService
				.getSchedulesByYearAndMonth(year, month);

		LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
		int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;
		int daysInMonth = firstDayOfMonth.lengthOfMonth();

		model.addAttribute("schedulesByDayAndTime", schedulesByDayAndTime);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("firstDayOfWeek", firstDayOfWeek);
		model.addAttribute("daysInMonth", daysInMonth);

		return "schedule/viewSchedules";
	}
	
	@GetMapping("/useView")
	public String useView(Model model) {
		LocalDate now = LocalDate.now();
		int year = now.getYear();
		int month = now.getMonthValue();

		Map<Integer, Map<String, List<Map<String, Object>>>> schedulesByDayAndTime = scheduleService
				.getSchedulesByYearAndMonth(year, month);

		LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
		int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;
		int daysInMonth = firstDayOfMonth.lengthOfMonth();

		model.addAttribute("schedulesByDayAndTime", schedulesByDayAndTime);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("firstDayOfWeek", firstDayOfWeek);
		model.addAttribute("daysInMonth", daysInMonth);

		return "schedule/useView";
	}
	
	@GetMapping("/miniView")
	public String miniView(@RequestParam("id") String employeeId ,Model model) {
		LocalDate now = LocalDate.now();
		int year = now.getYear();
		int month = now.getMonthValue();

		Map<Integer, Map<String, List<Map<String, Object>>>> schedulesByDayAndTime = scheduleService
				.getSchedulesByYearAndMonth(year, month);

		LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
		int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;
		int daysInMonth = firstDayOfMonth.lengthOfMonth();

		model.addAttribute("schedulesByDayAndTime", schedulesByDayAndTime);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("firstDayOfWeek", firstDayOfWeek);
		model.addAttribute("daysInMonth", daysInMonth);

		return "schedule/miniView";
	}
	




	@GetMapping("/search")
	@ResponseBody
	public Map<String, Object> searchMonth(@RequestParam int year, @RequestParam int month) {

		Map<Integer, Map<String, List<Map<String, Object>>>> schedulesByDayAndTime = scheduleService
				.getSchedulesByYearAndMonth(year, month);

		LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
		int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;
		int daysInMonth = firstDayOfMonth.lengthOfMonth();

		Map<String, Object> response = new HashMap<>();
		response.put("year", year);
		response.put("month", month);
		response.put("schedules", schedulesByDayAndTime);
		response.put("firstDayOfWeek", firstDayOfWeek);
		response.put("daysInMonth", daysInMonth);

		return response;
	}

	@GetMapping("/previousMonth")
	@ResponseBody
	public Map<String, Object> previousMonth(@RequestParam int year, @RequestParam int month) {
		Map<String, Integer> previousMonth = scheduleService.getPreviousMonth(year, month);
		int previousYear = previousMonth.get("year");
		int previousMonthValue = previousMonth.get("month");

		Map<Integer, Map<String, List<Map<String, Object>>>> schedulesByDayAndTime = scheduleService
				.getSchedulesByYearAndMonth(previousYear, previousMonthValue);

		LocalDate firstDayOfMonth = LocalDate.of(previousYear, previousMonthValue, 1);
		int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;
		int daysInMonth = firstDayOfMonth.lengthOfMonth();

		Map<String, Object> response = new HashMap<>();
		response.put("year", previousYear);
		response.put("month", previousMonthValue);
		response.put("schedules", schedulesByDayAndTime);
		response.put("firstDayOfWeek", firstDayOfWeek);
		response.put("daysInMonth", daysInMonth);

		return response;
	}

	@GetMapping("/nextMonth")
	@ResponseBody
	public Map<String, Object> nextMonth(@RequestParam int year, @RequestParam int month) {
		Map<String, Integer> nextMonth = scheduleService.getNextMonth(year, month);
		int nextYear = nextMonth.get("year");
		int nextMonthValue = nextMonth.get("month");

		Map<Integer, Map<String, List<Map<String, Object>>>> schedulesByDayAndTime = scheduleService
				.getSchedulesByYearAndMonth(nextYear, nextMonthValue);

		LocalDate firstDayOfMonth = LocalDate.of(nextYear, nextMonthValue, 1);
		int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;
		int daysInMonth = firstDayOfMonth.lengthOfMonth();

		Map<String, Object> response = new HashMap<>();
		response.put("year", nextYear);
		response.put("month", nextMonthValue);
		response.put("schedules", schedulesByDayAndTime);
		response.put("firstDayOfWeek", firstDayOfWeek);
		response.put("daysInMonth", daysInMonth);
		return response;
	}

	@GetMapping("/getEmployee")
	@ResponseBody
	public ResponseEntity<?> getAllEmployees() {
		try {
			List<EmpBean> employees = empService.findAllEmployees(false);
			List<Map<String, String>> employeeList = employees.stream()
					.map(emp -> Map.of("id", emp.getEmployeeId(), "name", emp.getEmployeeName()))
					.collect(Collectors.toList());
			System.out.println("Employee List: " + employeeList);
			return ResponseEntity.ok(employeeList);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("獲取員工資料失敗：" + e.getMessage());
		}
	}

	@PostMapping("/update")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateSchedule(@RequestParam String scheduleId, 
	                                                          @RequestParam int year,
	                                                          @RequestParam int month, 
	                                                          @RequestParam int day, 
	                                                          @RequestParam String startTime,
	                                                          @RequestParam String endTime, 
	                                                          @RequestParam List<String> employeeIds) {
	    try {
	        List<Integer> scheduleIds = Arrays.stream(scheduleId.split(","))
	                .map(Integer::parseInt)
	                .collect(Collectors.toList());

	        scheduleService.updateSchedule(scheduleIds, year, month, day, startTime, endTime, employeeIds);

	        Map<Integer, Map<String, List<Map<String, Object>>>> schedulesByDayAndTime = scheduleService.getSchedulesByYearAndMonth(year, month);

	        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
	        int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;
	        int daysInMonth = firstDayOfMonth.lengthOfMonth();

	        Map<String, Object> response = new HashMap<>();
	        response.put("year", year);
	        response.put("month", month);
	        response.put("schedules", schedulesByDayAndTime);
	        response.put("firstDayOfWeek", firstDayOfWeek);
	        response.put("daysInMonth", daysInMonth);

	        return ResponseEntity.ok(response);
	    } catch (NumberFormatException e) {
	        Map<String, Object> errorResponse = new HashMap<>();
	        errorResponse.put("error", "排班 ID 格式無效: " + e.getMessage());
	        return ResponseEntity.badRequest().body(errorResponse);
	    } catch (Exception e) {
	        Map<String, Object> errorResponse = new HashMap<>();
	        errorResponse.put("error", "發生錯誤: " + e.getMessage());
	        return ResponseEntity.badRequest().body(errorResponse);
	    }
	}



	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addSchedule(@RequestParam int year, 
	                                                       @RequestParam int month, 
	                                                       @RequestParam int day,
	                                                       @RequestParam String startTime, 
	                                                       @RequestParam String endTime, 
	                                                       @RequestParam List<String> employeeIds) {
	   
	    scheduleService.addSchedules(year, month, day, startTime, endTime, employeeIds);
	    
	    Map<Integer, Map<String, List<Map<String, Object>>>> schedulesByDayAndTime = scheduleService.getSchedulesByYearAndMonth(year, month);

	    LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
	    int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7; 
	    int daysInMonth = firstDayOfMonth.lengthOfMonth();

	    Map<String, Object> response = new HashMap<>();
	    response.put("year", year);
	    response.put("month", month);
	    response.put("schedules", schedulesByDayAndTime);
	    response.put("firstDayOfWeek", firstDayOfWeek);
	    response.put("daysInMonth", daysInMonth);

	    return ResponseEntity.ok(response); 
	}


	@PostMapping("/delete")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteSchedules(@RequestParam List<String> scheduleIds, 
	                                                           @RequestParam int year, 
	                                                           @RequestParam int month) {
	    try {
	        List<Integer> intScheduleIds = scheduleIds.stream().map(Integer::parseInt).collect(Collectors.toList());
	        scheduleService.deleteSchedules(intScheduleIds);
	        
	        Map<Integer, Map<String, List<Map<String, Object>>>> schedulesByDayAndTime = scheduleService.getSchedulesByYearAndMonth(year, month);
	        
	        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
	        int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;
	        int daysInMonth = firstDayOfMonth.lengthOfMonth();

	        Map<String, Object> response = new HashMap<>();
	        response.put("year", year);
	        response.put("month", month);
	        response.put("schedules", schedulesByDayAndTime);
	        response.put("firstDayOfWeek", firstDayOfWeek);
	        response.put("daysInMonth", daysInMonth);
	        response.put("message", "排班刪除成功");
	        
	        return ResponseEntity.ok(response);
	    } catch (NumberFormatException e) {
	        Map<String, Object> errorResponse = new HashMap<>();
	        errorResponse.put("error", "無效的排班 ID 格式: " + e.getMessage());
	        return ResponseEntity.badRequest().body(errorResponse);
	    } catch (Exception e) {
	        Map<String, Object> errorResponse = new HashMap<>();
	        errorResponse.put("error", "發生錯誤: " + e.getMessage());
	        return ResponseEntity.badRequest().body(errorResponse);
	    }
	}


	@GetMapping("/searchDateTime")
	@ResponseBody
	public List<ScheduleBean> getSchedules(@RequestParam String scheduleDate, @RequestParam String startTime,
			@RequestParam String endTime) {

		List<ScheduleBean> schedules = scheduleService.findSchedulesByDateAndTime(scheduleDate, startTime, endTime);

		return schedules;
	}

	@GetMapping("/totalHours")
	@ResponseBody
	public Map<String, Integer> getTotalHoursByMonth(@RequestParam int year, @RequestParam int month) {
		Map<String, Integer> schedules = scheduleService.getTotalHoursByMonth(year, month);
		return schedules;
	}
}