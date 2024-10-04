package marketMaster.controller.employee;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import marketMaster.bean.employee.EmpBean;
import marketMaster.bean.employee.RankLevelBean;
import marketMaster.exception.EmpDataAccessException;
import marketMaster.service.employee.EmployeeService;
import marketMaster.viewModel.EmployeeViewModel;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;
	
	@GetMapping("/empMain")
	public String showEmployeeMain() {
		return "employee/EmployeeMain";
	}
	
	@GetMapping("/empList")
	public String getAllEmployee(@RequestParam(defaultValue = "false") boolean showAll, Model model) {
		List<EmpBean> employees = employeeService.getAllEmployees(showAll);
		List<RankLevelBean> ranks = employeeService.getRankList();
		model.addAttribute("employees", employees);
		model.addAttribute("ranks", ranks);
		model.addAttribute("showAll", showAll);
		return "employee/EmployeeList";
	}
	
	@GetMapping("/empAddForm")
	public String showAddForm(Model model) {
		String newEmployeeId = employeeService.generateNewEmployeeId();
        model.addAttribute("newEmployeeId", newEmployeeId);
        model.addAttribute("emp", new EmpBean());
        return "employee/AddEmployee";
	}
	
	@PostMapping("/empAdd")
	public String addEmployee(@ModelAttribute("emp") EmpBean emp ,@RequestParam("file") MultipartFile file, Model model) {
		emp.setHiredate(LocalDate.now());
		emp.setPassword("0000");
		emp.setFirstLogin(true);
		
        try {
            String imagePath = employeeService.saveImage(file);
            emp.setImagePath(imagePath);
            
            boolean success = employeeService.addEmployee(emp);
            if (success) {
                model.addAttribute("message", "員工新增成功，新員工編號為: " + emp.getEmployeeId());
            } else {
                model.addAttribute("message", "新增失敗");
            }
        } catch (Exception e) {
            model.addAttribute("message", "新增失敗：" + e.getMessage());
        }
        return "employee/ResultEmp";
	}
	
    @GetMapping("/getUpdate")
    public String getEmployeeForUpdate(@RequestParam String employeeId, Model model) {
            EmployeeViewModel empViewModel = employeeService.getEmployeeViewModel(employeeId);
            model.addAttribute("employeeToUpdate", empViewModel);
            return "employee/UpdateEmployee";
    }
	
    @PostMapping("/empUpdate")
    public String updateEmployee(@ModelAttribute("emp") EmpBean emp, 
                                 @RequestParam(value = "file", required = false) MultipartFile file,
                                 Model model) {
        try {
            boolean success = employeeService.updateEmployee(emp, file);
            if (success) {
                model.addAttribute("message", "員工更新成功");
            } else {
                model.addAttribute("message", "更新失敗，員工不存在");
            }
        } catch (EmpDataAccessException e) {
            model.addAttribute("message", "更新失敗：" + e.getMessage());
        }
        return "employee/ResultEmp";
    }
	
}
