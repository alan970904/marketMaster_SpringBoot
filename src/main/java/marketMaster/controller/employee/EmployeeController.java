package marketMaster.controller.employee;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import marketMaster.bean.employee.EmpBean;
import marketMaster.bean.employee.RankLevelBean;
import marketMaster.exception.EmpDataAccessException;
import marketMaster.service.AuthorizationService;
import marketMaster.service.employee.EmployeeServiceImpl;
import marketMaster.viewModel.EmployeeViewModel;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

	@Autowired
	private EmployeeServiceImpl employeeService;

	@Autowired
	private AuthorizationService authService;

	@GetMapping("/empList")
	public String getAllEmployee(@RequestParam(defaultValue = "false") boolean showAll,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, Model model,
			HttpSession session) {
		EmployeeViewModel currentEmployee = (EmployeeViewModel) session.getAttribute("employee");
		int authority = currentEmployee.getAuthority();

		if (!authService.hasPermission(authority, "viewList", "employee")) {
			return "redirect:/unauthorized";
		}

		Page<EmpBean> employeePage = employeeService.getAllEmployees(showAll, PageRequest.of(page, size));
		List<RankLevelBean> ranks = null;

		if (authority >= 2) {
			ranks = employeeService.getRankList(); // 只有權限2和3可以看到職級列表
		}

		model.addAttribute("employees", employeePage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", employeePage.getTotalPages());
		model.addAttribute("totalItems", employeePage.getTotalElements());
		model.addAttribute("currentAuthority", authority);
		model.addAttribute("showAll", showAll);

		if (ranks != null) {
			model.addAttribute("ranks", ranks);
		}

		model.addAttribute("isSearchResult", false);

		return "employee/EmployeeList";
	}

	@GetMapping("/empAddForm")
	public String showAddForm(Model model) {
		String newEmployeeId = employeeService.generateNewEmployeeId();
		model.addAttribute("newEmployeeId", newEmployeeId);
		model.addAttribute("emp", new EmpBean());
		model.addAttribute("positions", employeeService.getAllPositions());
		return "employee/AddEmployee";
	}

	@PostMapping("/empAdd")
	@ResponseBody
	public ResponseEntity<?> addEmployee(@ModelAttribute("emp") EmpBean emp, @RequestParam("file") MultipartFile file) {
		try {
			// 處理新增員工的邏輯
			boolean success = employeeService.addEmployee(emp, file);
			if (success) {
				return ResponseEntity.ok().body("Employee added successfully");
			} else {
				return ResponseEntity.badRequest().body("Failed to add employee");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
		}
	}

	@GetMapping("/search")
	public String searchEmployees(@RequestParam String searchName,
			@RequestParam(defaultValue = "false") boolean showAll, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, Model model, HttpSession session) {

		EmployeeViewModel currentEmployee = (EmployeeViewModel) session.getAttribute("employee");
		int authority = currentEmployee.getAuthority();

		Page<EmpBean> employeePage = employeeService.searchEmployees(searchName, showAll, PageRequest.of(page, size));
		List<RankLevelBean> ranks = null;

		if (authority >= 2) {
			ranks = employeeService.getRankList();
		}

		model.addAttribute("employees", employeePage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", employeePage.getTotalPages());
		model.addAttribute("totalItems", employeePage.getTotalElements());
		model.addAttribute("currentAuthority", authority);
		model.addAttribute("showAll", showAll);
		model.addAttribute("searchName", searchName);

		if (ranks != null) {
			model.addAttribute("ranks", ranks);
		}

		model.addAttribute("isSearchResult", true);

		return "employee/EmployeeList";
	}

	@GetMapping("/details")
	public String showEmployeeDetails(@RequestParam String employeeId, Model model, HttpSession session) {
		EmployeeViewModel currentEmployee = (EmployeeViewModel) session.getAttribute("employee");
		int authority = currentEmployee.getAuthority();

		if (!authService.hasPermission(authority, "view", "employee")
				|| (authority == 1 && !currentEmployee.getEmployeeId().equals(employeeId))) {
			return "redirect:/unauthorized";
		}

		EmployeeViewModel employee = employeeService.getEmployeeViewModel(employeeId);
		model.addAttribute("employee", employee);
		model.addAttribute("currentAuthority", authority);
		return "employee/EmployeeDetails";
	}

	@GetMapping("/getUpdate")
	public String getEmployeeForUpdate(@RequestParam String employeeId, Model model, HttpSession session) {
		EmployeeViewModel currentEmployee = (EmployeeViewModel) session.getAttribute("employee");
		int authority = currentEmployee.getAuthority();

		if (!authService.hasPermission(authority, "update", "employee")
				|| (authority == 1 && !currentEmployee.getEmployeeId().equals(employeeId))) {
			return "redirect:/unauthorized";
		}

		EmployeeViewModel empViewModel = employeeService.getEmployeeViewModel(employeeId);

		if (authority == 2 && empViewModel.getAuthority() == 3) {
			return "redirect:/unauthorized";
		}

		model.addAttribute("employeeToUpdate", empViewModel);
		model.addAttribute("positions", employeeService.getAllPositions());
		model.addAttribute("currentAuthority", authority);
		return "employee/UpdateEmployee";
	}

	@PostMapping("/empUpdate")
	public String updateEmployee(@ModelAttribute("emp") EmpBean emp,
			@RequestParam(value = "file", required = false) MultipartFile file, RedirectAttributes redirectAttributes,
			HttpSession session) {
		EmployeeViewModel currentEmployee = (EmployeeViewModel) session.getAttribute("employee");
		int authority = currentEmployee.getAuthority();

		if (!authService.hasPermission(authority, "update", "employee")
				|| (authority == 1 && !currentEmployee.getEmployeeId().equals(emp.getEmployeeId()))) {
			return "redirect:/unauthorized";
		}

		if (authority == 1) {
			// 權限1只能更新自己的某些欄位
			EmpBean existingEmp = employeeService.getEmployee(emp.getEmployeeId());
			existingEmp.setEmployeeName(emp.getEmployeeName());
			existingEmp.setEmployeeTel(emp.getEmployeeTel());
			emp = existingEmp;
		}

		try {
			boolean success = employeeService.updateEmployee(emp, file);
			if (success) {
				redirectAttributes.addFlashAttribute("message", "員工更新成功");
				return "redirect:/employee/details?employeeId=" + emp.getEmployeeId();
			} else {
				redirectAttributes.addFlashAttribute("message", "更新失敗，員工不存在");
				return "redirect:/employee/empList";
			}
		} catch (EmpDataAccessException e) {
			redirectAttributes.addFlashAttribute("message", "更新失敗：" + e.getMessage());
		}
		
		return "redirect:/employee/empList";
	}

	@GetMapping("/delete")
	public String deleteEmployee(@RequestParam String employeeId, RedirectAttributes redirectAttributes,
			HttpSession session) {
		EmployeeViewModel currentEmployee = (EmployeeViewModel) session.getAttribute("employee");
		int authority = currentEmployee.getAuthority();

		if (!authService.hasPermission(authority, "delete", "employee")) {
			return "redirect:/unauthorized";
		}
		
		// 添加額外檢查，確保權限2不能刪除權限3的用戶
	    EmpBean employeeToDelete = employeeService.getEmployee(employeeId);
	    if (authority == 2 && employeeToDelete.getAuthority() == 3) {
	        return "redirect:/unauthorized";
	    }

		boolean deleted = employeeService.deleteEmployee(employeeId);
		if (deleted) {
			redirectAttributes.addFlashAttribute("message", "員工已成功刪除");
		} else {
			redirectAttributes.addFlashAttribute("error", "刪除員工時出錯");
		}
		return "redirect:/employee/empList";
	}

	@GetMapping("/photo/{employeeId}")
	public ResponseEntity<Resource> getEmployeePhoto(@PathVariable String employeeId) {
		Resource file = employeeService.getEmployeePhoto(employeeId);
		if (file != null) {
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(file);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("/unauthorized")
	public String unauthorizedAccess(Model model) {
	    model.addAttribute("errorMessage", "權限不足，無法操作");
	    return "unauthorized";
	}
}
