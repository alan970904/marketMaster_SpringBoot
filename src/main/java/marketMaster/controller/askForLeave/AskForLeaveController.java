package marketMaster.controller.askForLeave;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import marketMaster.bean.askForLeave.AskForLeave;
import marketMaster.bean.employee.EmpBean;
import marketMaster.service.askForLeave.AskForLeaveService;
import marketMaster.service.employee.EmployeeService;

@Controller
public class AskForLeaveController {

	@Autowired
	private AskForLeaveService aslService;

	@Autowired
	private EmployeeService empService;

	@GetMapping("/askForLeave/home")
	public String homePage() {
		return "askForLeave/homeAsl";
	}

	@GetMapping("/askForLeave/search")
	public String searchAslByEmpId(@RequestParam("id") String employeeId, Model model) {
		EmpBean employee = empService.getEmployee(employeeId);
		if (employee == null) {
			model.addAttribute("error", "找不到該員工");
			return "askForLeave/homeAsl";
		}

		List<AskForLeave> askForLeaveList = aslService.findAslByEmpId(employeeId);

		model.addAttribute("empBean", employee); // 添加這行
		model.addAttribute("leaves", askForLeaveList);
		return "askForLeave/emPage";
	}

	@GetMapping("/askForLeave/image/{leaveId}")
	@ResponseBody
	public ResponseEntity<byte[]> getImage(@PathVariable String leaveId) {
		AskForLeave askForLeave = aslService.findAslById(leaveId);
		if (askForLeave != null && askForLeave.getProofImage() != null) {
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(askForLeave.getProofImage());
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

		model.addAttribute("askForLeave", newAsl);
		model.addAttribute("employeeId", employeeId);
		model.addAttribute("employeeName", empBean.getEmployeeName());

		return "askForLeave/addAsl";
	}

	@PostMapping("/askForLeave/addpost")
	public String addAsl(@RequestParam String employeeId, @RequestParam String leaveType,
			@RequestParam String reasonLeave,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime startTime,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime endTime,
			@RequestParam String approvedStatus, @RequestParam("proofImage") MultipartFile proofImageFile,
			Model model) {

		String leaveId = aslService.generateId();
		AskForLeave askForLeave = new AskForLeave();
		askForLeave.setLeaveId(leaveId);
		askForLeave.setLeaveType(leaveType);
		askForLeave.setReasonLeave(reasonLeave);
		askForLeave.setStarTime(startTime);
		askForLeave.setEndTime(endTime);
		askForLeave.setApprovedStatus(approvedStatus);

		EmpBean empBean = empService.getEmployee(employeeId);
		askForLeave.setEmpBean(empBean);

		try {
			byte[] proofImageBytes = proofImageFile.getBytes();
			askForLeave.setProofImage(proofImageBytes);
		} catch (IOException e) {
			model.addAttribute("error", "上傳檔案時發生錯誤");
			return null;
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
	            existAsl.setEmpBean(empBean); 
	            model.addAttribute("askForLeave", existAsl);
	            model.addAttribute("employeeName", empBean.getEmployeeName());
	        }
	        return "askForLeave/editAsl"; 
	    } else {
	        return null;
	    }
	}

	@PostMapping("/askForLeave/update/{id}")
	public String updateAsl(@PathVariable String id, @RequestParam String leaveType, @RequestParam String reasonLeave,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime startTime,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime endTime,
			@RequestParam String approvedStatus, @RequestParam(required = false) MultipartFile proofImage) {
		System.out.println("Updating leave with ID: " + id);
		AskForLeave existAsl = aslService.findAslById(id);

		if (existAsl != null) {
			existAsl.setLeaveType(leaveType);
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
}
