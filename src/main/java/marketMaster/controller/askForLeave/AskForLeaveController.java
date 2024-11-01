package marketMaster.controller.askForLeave;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import marketMaster.bean.askForLeave.AskForLeaveBean;
import marketMaster.bean.askForLeave.LeaveCategoryBean;
import marketMaster.bean.employee.EmpBean;
import marketMaster.bean.schedule.ScheduleBean;
import marketMaster.service.askForLeave.AskForLeaveService;
import marketMaster.service.askForLeave.LeaveCategoryService;
import marketMaster.service.askForLeave.LeaveRecordService;
import marketMaster.service.employee.EmployeeService;
import marketMaster.service.schedule.ScheduleService;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AskForLeaveController {

	@Autowired
	private AskForLeaveService aslService;

	@Autowired
	private EmployeeService empService;

	@Autowired
	private LeaveCategoryService leaveCategoryService;

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private LeaveRecordService leaveRecordService;

	@GetMapping("/askForLeave/search")
	public String findAslByEmpId(@RequestParam("id") String employeeId,
			@RequestParam(value = "p", defaultValue = "1") Integer pageNum, Model model) {

		EmpBean employee = empService.getEmployee(employeeId);

		Page<AskForLeaveBean> page = aslService.findAslByEmpId(employeeId, pageNum);

		model.addAttribute("empBean", employee);
		model.addAttribute("leaves", page.getContent());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());

		return "askForLeave/emPage";
	}

	@GetMapping("/askForLeave/usefind")
	public String useFindAslByEmpId(@RequestParam("id") String employeeId,
			@RequestParam(value = "p", defaultValue = "1") Integer pageNum, Model model) {

		EmpBean employee = empService.getEmployee(employeeId);

		Page<AskForLeaveBean> page = aslService.findAslByEmpId(employeeId, pageNum);

		model.addAttribute("empBean", employee);
		model.addAttribute("leaves", page.getContent());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());

		return "askForLeave/front/useEmPage";
	}

	@GetMapping("/askForLeave/waitStatus")
	public String waitStatus(@RequestParam("id") String employeeId, Model model) {
	    System.out.println("employeeId=" + employeeId);
	    List<AskForLeaveBean> aslBean = aslService.findAslByEmpIdStatus(employeeId);
	    model.addAttribute("leaves", aslBean);

	    return "askForLeave/front/waitStatus";
	}

	@GetMapping("/askForLeave/filter")
	@ResponseBody
	public ResponseEntity<Page<AskForLeaveBean>> filterAskForLeaves(@RequestParam(required = false) String employeeId,
			@RequestParam(required = false) String employeeName,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startTime,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endTime,
			@RequestParam(required = false) String leaveCategory, @RequestParam(required = false) String approvedStatus,
			@RequestParam(defaultValue = "1") int pageNum) {

		employeeId = (employeeId != null && !employeeId.isEmpty()) ? employeeId : null;
		employeeName = (employeeName != null && !employeeName.isEmpty()) ? employeeName : null;
		leaveCategory = (leaveCategory != null && !leaveCategory.isEmpty()) ? leaveCategory : null;
		approvedStatus = (approvedStatus != null && !approvedStatus.isEmpty()) ? approvedStatus : null;

		Page<AskForLeaveBean> result = aslService.filterFindAsl(employeeId, employeeName,
				startTime != null ? startTime.atStartOfDay() : null,
				endTime != null ? endTime.atTime(23, 59, 59) : null, leaveCategory, approvedStatus, pageNum);

		return ResponseEntity.ok(result);
	}

	@GetMapping("/askForLeave/image/{leaveId}")
	@ResponseBody
	public ResponseEntity<byte[]> getImage(@PathVariable String leaveId) {
		AskForLeaveBean askForLeave = aslService.findAslById(leaveId);
		if (askForLeave != null) {

			MediaType mediaType = aslService.getMediaTypeForFile(askForLeave.getProofImage());

			return ResponseEntity.ok().contentType(mediaType).body(askForLeave.getProofImage());
		}
		return ResponseEntity.notFound().build();
	}

	@GetMapping("/askForLeave/add")
	public String showAddAslForm(@RequestParam String employeeId, Model model) {
		EmpBean empBean = empService.getEmployee(employeeId);
		if (empBean == null) {
			model.addAttribute("error", "找不到該員工");
			return null;
		}

		AskForLeaveBean newAsl = new AskForLeaveBean();
		newAsl.setLeaveId(aslService.generateId());
		newAsl.setEmpBean(empBean);

		List<LeaveCategoryBean> leaveCategories = leaveCategoryService.findAllLeaveCategories();

		model.addAttribute("askForLeave", newAsl);
		model.addAttribute("employeeId", employeeId);
		model.addAttribute("employeeName", empBean.getEmployeeName());
		model.addAttribute("leaveCategories", leaveCategories);

		return "askForLeave/addAsl";
	}

	@GetMapping("/askForLeave/useAdd")
	public String useAddAslForm(@RequestParam String employeeId, Model model) {
		EmpBean empBean = empService.getEmployee(employeeId);
		if (empBean == null) {
			model.addAttribute("error", "找不到該員工");
			return null;
		}

		AskForLeaveBean newAsl = new AskForLeaveBean();
		newAsl.setLeaveId(aslService.generateId());
		newAsl.setEmpBean(empBean);

		List<LeaveCategoryBean> leaveCategories = leaveCategoryService.findAllLeaveCategories();

		model.addAttribute("askForLeave", newAsl);
		model.addAttribute("employeeId", employeeId);
		model.addAttribute("employeeName", empBean.getEmployeeName());
		model.addAttribute("leaveCategories", leaveCategories);

		return "askForLeave/front/useAddAsl";
	}

	@PostMapping("/askForLeave/addpost")
	public String addAsl(@RequestParam String employeeId, @RequestParam Integer categoryId,
			@RequestParam String reasonLeave,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime startTime,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime endTime,
			@RequestParam String approvedStatus, @RequestParam("proofImage") MultipartFile proofImageFile, Model model,
			RedirectAttributes redirectAtb) {

		if (aslService.isLeaveTimeOverlapping(employeeId, startTime, endTime)) {
			redirectAtb.addFlashAttribute("duplicateError", "同時間已有請假，不可重複");
			return "redirect:/askForLeave/search?id=" + employeeId;
		}

		List<ScheduleBean> schedulesByDateTimeRange = scheduleService.findSchedulesByDateTimeRange(employeeId,
				startTime, endTime);
		int totalScheduleHours = schedulesByDateTimeRange.stream().mapToInt(ScheduleBean::getScheduleHour).sum();
		if (schedulesByDateTimeRange.isEmpty()) {
			redirectAtb.addFlashAttribute("scheduleError", "沒有找到排班，請檢查時間範圍");
			return "redirect:/askForLeave/search?id=" + employeeId;
		}

		String leaveId = aslService.generateId();
		AskForLeaveBean askForLeave = new AskForLeaveBean();
		askForLeave.setLeaveId(leaveId);
		LeaveCategoryBean leaveCategory = leaveCategoryService.getLeaveCategoryById(categoryId);
		askForLeave.setLeaveCategory(leaveCategory);
		askForLeave.setReasonLeave(reasonLeave);
		askForLeave.setStarTime(startTime);
		askForLeave.setEndTime(endTime);
		askForLeave.setLeaveHours(totalScheduleHours);
		askForLeave.setApprovedStatus(approvedStatus);

		EmpBean empBean = empService.getEmployee(employeeId);
		askForLeave.setEmpBean(empBean);

		if (!proofImageFile.isEmpty()) {
			try {
				byte[] proofImageBytes = proofImageFile.getBytes();
				askForLeave.setProofImage(proofImageBytes);
			} catch (IOException e) {
				model.addAttribute("fileError", "上傳檔案時發生錯誤");
				return null;
			}
		} else {
			askForLeave.setProofImage(null);
		}
		aslService.saveAsl(askForLeave);
		return "redirect:/askForLeave/search?id=" + employeeId;
	}

	@PostMapping("/askForLeave/useAddpost")
	public String useAddAsl(@RequestParam String employeeId, @RequestParam Integer categoryId,
			@RequestParam String reasonLeave,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime startTime,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime endTime,
			@RequestParam String approvedStatus, @RequestParam("proofImage") MultipartFile proofImageFile, Model model,
			RedirectAttributes redirectAtb) {

		if (aslService.isLeaveTimeOverlapping(employeeId, startTime, endTime)) {
			redirectAtb.addFlashAttribute("duplicateError", "同時間已有請假，不可重複");
			return "redirect:/askForLeave/usefind?id=" + employeeId;
		}

		List<ScheduleBean> schedulesByDateTimeRange = scheduleService.findSchedulesByDateTimeRange(employeeId,
				startTime, endTime);
		int totalScheduleHours = schedulesByDateTimeRange.stream().mapToInt(ScheduleBean::getScheduleHour).sum();
		if (schedulesByDateTimeRange.isEmpty()) {
			redirectAtb.addFlashAttribute("scheduleError", "沒有找到排班，請檢查時間範圍");
			return "redirect:/askForLeave/usefind?id=" + employeeId;
		}

		String leaveId = aslService.generateId();
		AskForLeaveBean askForLeave = new AskForLeaveBean();
		askForLeave.setLeaveId(leaveId);
		LeaveCategoryBean leaveCategory = leaveCategoryService.getLeaveCategoryById(categoryId);
		askForLeave.setLeaveCategory(leaveCategory);
		askForLeave.setReasonLeave(reasonLeave);
		askForLeave.setStarTime(startTime);
		askForLeave.setEndTime(endTime);
		askForLeave.setLeaveHours(totalScheduleHours);
		askForLeave.setApprovedStatus(approvedStatus);

		EmpBean empBean = empService.getEmployee(employeeId);
		askForLeave.setEmpBean(empBean);

		if (!proofImageFile.isEmpty()) {
			try {
				byte[] proofImageBytes = proofImageFile.getBytes();
				askForLeave.setProofImage(proofImageBytes);
			} catch (IOException e) {
				model.addAttribute("fileError", "上傳檔案時發生錯誤");
				return null;
			}
		} else {
			askForLeave.setProofImage(null);
		}
		aslService.saveAsl(askForLeave);
		return "redirect:/askForLeave/usefind?id=" + employeeId;
	}

	@GetMapping("/askForLeave/edit/{id}")
	public String showEditAslForm(@PathVariable String id, Model model) {
		AskForLeaveBean existAsl = aslService.findAslById(id);
		if (existAsl != null) {

			EmpBean empBean = empService.getEmployee(existAsl.getEmpBean().getEmployeeId());

			if (empBean != null) {
				List<LeaveCategoryBean> leaveCategories = leaveCategoryService.findAllLeaveCategories();
				existAsl.setEmpBean(empBean);
				model.addAttribute("askForLeave", existAsl);
				model.addAttribute("employeeName", empBean.getEmployeeName());
				model.addAttribute("leaveCategories", leaveCategories);
			}
			return "askForLeave/editAsl";
		} else {
			return null;
		}
	}

	@GetMapping("/askForLeave/useEdit/{id}")
	public String useEditAslForm(@PathVariable String id, Model model) {
		AskForLeaveBean existAsl = aslService.findAslById(id);
		if (existAsl != null) {

			EmpBean empBean = empService.getEmployee(existAsl.getEmpBean().getEmployeeId());

			if (empBean != null) {
				List<LeaveCategoryBean> leaveCategories = leaveCategoryService.findAllLeaveCategories();
				existAsl.setEmpBean(empBean);
				model.addAttribute("askForLeave", existAsl);
				model.addAttribute("employeeName", empBean.getEmployeeName());
				model.addAttribute("leaveCategories", leaveCategories);
			}
			return "askForLeave/front/useEditAsl";
		} else {
			return null;
		}
	}

	@PostMapping("/askForLeave/update/{id}")
	public String updateAsl(@PathVariable String id, @RequestParam String employeeId, @RequestParam Integer categoryId,
			@RequestParam String reasonLeave,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime startTime,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime endTime,
			@RequestParam String approvedStatus, @RequestParam(required = false) MultipartFile proofImage,
			RedirectAttributes redirectAtb) {

		AskForLeaveBean existAsl = aslService.findAslById(id);

		if (existAsl != null) {

			List<ScheduleBean> addschedulesTimeRange = scheduleService.findSchedulesByDateTimeRange(employeeId,
					startTime, endTime);
			if (addschedulesTimeRange.isEmpty()) {
				redirectAtb.addFlashAttribute("scheduleError", "沒有找到排班，請檢查時間範圍");
				return "redirect:/askForLeave/search?id=" + employeeId;
			}
			int addScheduleHours = addschedulesTimeRange.stream().mapToInt(ScheduleBean::getScheduleHour).sum();

			LeaveCategoryBean leaveCategory = leaveCategoryService.getLeaveCategoryById(categoryId);
			existAsl.setLeaveCategory(leaveCategory);
			existAsl.setReasonLeave(reasonLeave);
			existAsl.setStarTime(startTime);
			existAsl.setEndTime(endTime);
			existAsl.setLeaveHours(addScheduleHours);
			existAsl.setApprovedStatus(approvedStatus);

			if (proofImage != null && !proofImage.isEmpty()) {
				try {
					existAsl.setProofImage(proofImage.getBytes());
				} catch (IOException e) {
				}
			}

			aslService.saveAsl(existAsl);
			return "redirect:/askForLeave/search?id=" + existAsl.getEmpBean().getEmployeeId();
		} else {
			return null;
		}
	}

	@PostMapping("/askForLeave/useUpdate/{id}")
	public String useUpdateAsl(@PathVariable String id, @RequestParam String employeeId,
			@RequestParam Integer categoryId, @RequestParam String reasonLeave,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime startTime,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime endTime,
			@RequestParam String approvedStatus, @RequestParam(required = false) MultipartFile proofImage,
			RedirectAttributes redirectAtb) {

		AskForLeaveBean existAsl = aslService.findAslById(id);

		if (existAsl != null) {

			List<ScheduleBean> addschedulesTimeRange = scheduleService.findSchedulesByDateTimeRange(employeeId,
					startTime, endTime);
			if (addschedulesTimeRange.isEmpty()) {
				redirectAtb.addFlashAttribute("scheduleError", "沒有找到排班，請檢查時間範圍");
				return "redirect:/askForLeave/usefind?id=" + employeeId;
			}
			int addScheduleHours = addschedulesTimeRange.stream().mapToInt(ScheduleBean::getScheduleHour).sum();

			LeaveCategoryBean leaveCategory = leaveCategoryService.getLeaveCategoryById(categoryId);
			existAsl.setLeaveCategory(leaveCategory);
			existAsl.setReasonLeave(reasonLeave);
			existAsl.setStarTime(startTime);
			existAsl.setEndTime(endTime);
			existAsl.setLeaveHours(addScheduleHours);
			existAsl.setApprovedStatus(approvedStatus);

			if (proofImage != null && !proofImage.isEmpty()) {
				try {
					existAsl.setProofImage(proofImage.getBytes());
				} catch (IOException e) {
				}
			}

			aslService.saveAsl(existAsl);
			return "redirect:/askForLeave/usefind?id=" + existAsl.getEmpBean().getEmployeeId();
		} else {
			return null;
		}
	}

	@DeleteMapping("/askForLeave/delete/{leaveId}")
	@ResponseBody
	public ResponseEntity<String> deleteLeave(@PathVariable String leaveId) {
		try {
			aslService.deleteAsl(leaveId);
			return ResponseEntity.ok("刪除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("刪除失敗：" + e.getMessage());
		}
	}

	@GetMapping("/askForLeave/approval")
	public String findByRank(@RequestParam("id") String employeeId,
			@RequestParam(value = "p", defaultValue = "1") Integer pageNum, Model model) {

		Page<AskForLeaveBean> askForLeave = aslService.findAllByRank(employeeId, pageNum);
		EmpBean employee = empService.getEmployee(employeeId);

		model.addAttribute("empBean", employee);
		model.addAttribute("askForLeave", askForLeave);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", askForLeave.getTotalPages());

		return "askForLeave/approval";
	}

	@PostMapping("/askForLeave/approvalpost")
	@ResponseBody
	public ResponseEntity<String> approveLeave(@RequestParam String leaveId) {
		try {
			AskForLeaveBean aslById = aslService.findAslById(leaveId);
			String employeeId = aslById.getEmpBean().getEmployeeId();
			Integer leaveHours = aslById.getLeaveHours();
			LocalDateTime endTime = aslById.getEndTime();
			Integer categoryId = aslById.getLeaveCategory().getCategoryId();

			leaveRecordService.checkLeaveRecord(employeeId, categoryId, endTime);
			leaveRecordService.addLeaveHours(employeeId, categoryId, endTime, leaveHours);
			aslService.approveLeave(leaveId);
			return ResponseEntity.ok("請假申請已核准");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("核准失敗：" + e.getMessage());
		}
	}

	@PostMapping("/askForLeave/reject")
	@ResponseBody
	public ResponseEntity<String> rejectLeave(@RequestParam String leaveId, @RequestParam String rejectionReason) {
		try {
			AskForLeaveBean aslById = aslService.findAslById(leaveId);
			String employeeId = aslById.getEmpBean().getEmployeeId();
			Integer leaveHours = aslById.getLeaveHours();
			LocalDateTime endTime = aslById.getEndTime();
			Integer categoryId = aslById.getLeaveCategory().getCategoryId();

			leaveRecordService.minusLeaveHours(employeeId, categoryId, endTime, leaveHours);

			aslService.rejectLeave(leaveId, rejectionReason);
			return ResponseEntity.ok("請假申請已拒絕");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("拒絕失敗：" + e.getMessage());
		}
	}

}
