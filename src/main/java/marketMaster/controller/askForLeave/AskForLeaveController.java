package marketMaster.controller.askForLeave;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
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

import marketMaster.bean.askForLeave.AskForLeave;
import marketMaster.bean.askForLeave.LeaveCategory;
import marketMaster.bean.employee.EmpBean;
import marketMaster.service.askForLeave.AskForLeaveService;
import marketMaster.service.askForLeave.LeaveCategoryService;
import marketMaster.service.askForLeave.LeaveRecordService;
import marketMaster.service.employee.EmployeeServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AskForLeaveController {

	@Autowired
	private AskForLeaveService aslService;

	@Autowired
	private EmployeeServiceImpl empService;

	@Autowired
	private LeaveCategoryService leaveCategoryService;

	@Autowired
	private LeaveRecordService leaveRecordService;

	@GetMapping("/askForLeave/home")
	public String homePage() {
		return "askForLeave/homeAsl";
	}

	@GetMapping("/askForLeave/search")
	public String findAslByEmpId(@RequestParam("id") String employeeId,
			@RequestParam(value = "p", defaultValue = "1") Integer pageNum, Model model) {
		EmpBean employee = empService.getEmployee(employeeId);

		if (employee == null) {
			model.addAttribute("error", "找不到該員工");
			return "askForLeave/homeAsl";
		}

		Page<AskForLeave> page = aslService.findAslByEmpId(employeeId, pageNum);

		model.addAttribute("empBean", employee);
		model.addAttribute("leaves", page.getContent());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());

		return "askForLeave/emPage";
	}

	@GetMapping("/askForLeave/image/{leaveId}")
	@ResponseBody
	public ResponseEntity<byte[]> getImage(@PathVariable String leaveId) {
		AskForLeave askForLeave = aslService.findAslById(leaveId);
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

		AskForLeave newAsl = new AskForLeave();
		newAsl.setLeaveId(aslService.generateId());
		newAsl.setEmpBean(empBean);

		List<LeaveCategory> leaveCategories = leaveCategoryService.findAllLeaveCategories();

		model.addAttribute("askForLeave", newAsl);
		model.addAttribute("employeeId", employeeId);
		model.addAttribute("employeeName", empBean.getEmployeeName());
		model.addAttribute("leaveCategories", leaveCategories);

		return "askForLeave/addAsl";
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

		String leaveId = aslService.generateId();
		AskForLeave askForLeave = new AskForLeave();
		askForLeave.setLeaveId(leaveId);
		LeaveCategory leaveCategory = leaveCategoryService.getLeaveCategoryById(categoryId);
		askForLeave.setLeaveCategory(leaveCategory);
		askForLeave.setReasonLeave(reasonLeave);
		askForLeave.setStarTime(startTime);
		askForLeave.setEndTime(endTime);
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

	@GetMapping("/askForLeave/edit/{id}")
	public String showEditAslForm(@PathVariable String id, Model model) {
		AskForLeave existAsl = aslService.findAslById(id);
		if (existAsl != null) {

			EmpBean empBean = empService.getEmployee(existAsl.getEmpBean().getEmployeeId());

			if (empBean != null) {
				List<LeaveCategory> leaveCategories = leaveCategoryService.findAllLeaveCategories();
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

	@PostMapping("/askForLeave/update/{id}")
	public String updateAsl(@PathVariable String id, @RequestParam String employeeId, @RequestParam Integer categoryId,
			@RequestParam String reasonLeave,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime startTime,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime endTime,
			@RequestParam String approvedStatus, @RequestParam(required = false) MultipartFile proofImage,
			RedirectAttributes redirectAtb) {

		AskForLeave existAsl = aslService.findAslById(id);

		if (existAsl != null) {
			if (!startTime.equals(existAsl.getStarTime()) || !endTime.equals(existAsl.getEndTime())) {
				if (aslService.isLeaveTimeOverlapping(employeeId, startTime, endTime)) {
					redirectAtb.addFlashAttribute("duplicateError", "同時間已有請假，不可重複");
					return "redirect:/askForLeave/search?id=" + employeeId;
				}
			}

			LeaveCategory leaveCategory = leaveCategoryService.getLeaveCategoryById(categoryId);
			existAsl.setLeaveCategory(leaveCategory);
			existAsl.setReasonLeave(reasonLeave);
			existAsl.setStarTime(startTime);
			existAsl.setEndTime(endTime);
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

	@DeleteMapping("/askForLeave/delete/{leaveId}")
	@ResponseBody
	public ResponseEntity<String> deleteLeave(@PathVariable String leaveId) {
		try {
			System.out.println("Attempting to delete leave with ID: " + leaveId); // 輸出請假 ID
			aslService.deleteAsl(leaveId);
			return ResponseEntity.ok("刪除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("刪除失敗：" + e.getMessage());
		}
	}

	@GetMapping("/askForLeave/findAll")
	public String findAllAsl(@RequestParam(value = "p", defaultValue = "1") Integer pageNum, Model model) {
		Page<AskForLeave> page = aslService.findAslPage(pageNum);
		model.addAttribute("askForLeaves", page.getContent());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		return "askForLeave/showAll";
	}

	@GetMapping("/askForLeave/approval")
	public String findByRank(@RequestParam("id") String employeeId,
			@RequestParam(value = "p", defaultValue = "1") Integer pageNum, Model model) {

		Page<AskForLeave> askForLeave = aslService.findAllByRank(employeeId, pageNum);
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
			System.out.println("----------------------------------------");
			System.out.println("leaveId="+leaveId);
			aslService.rejectLeave(leaveId, rejectionReason);
			return ResponseEntity.ok("請假申請已拒絕");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("拒絕失敗：" + e.getMessage());
		}
	}

}
