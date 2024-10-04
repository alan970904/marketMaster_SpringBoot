package marketMaster.controller.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import marketMaster.bean.employee.EmpBean;
import marketMaster.service.employee.EmployeeService;
import marketMaster.viewModel.EmployeeViewModel;

@Controller
public class LoginController {

	@Autowired
	private EmployeeService employeeService;
	
	@GetMapping("/loginPage")
	public String showLoginPage() {
		return "employee/Login";
	}
	
    @PostMapping("/empLogin")
    public String login(@RequestParam String employeeId, @RequestParam String password, 
                        HttpSession session, Model model) {
        try {
            EmpBean employee = employeeService.login(employeeId, password);
            if (employee != null) {
                EmployeeViewModel employeeViewModel = employeeService.getEmployeeViewModel(employeeId);
                session.setAttribute("employee", employeeViewModel);

                if (employee.isFirstLogin()) {
                    return "redirect:/changePasswordPage";
                } else {
                    return "redirect:/homePage";
                }
            } else {
                model.addAttribute("errorMessage", "員工編號或密碼錯誤");
                return "employee/Login";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "系統錯誤: " + e.getMessage());
            return "employee/Login";
        }
    }

    @GetMapping("/emplogout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/empLogin";
    }
    
    @GetMapping("/homePage")
    public String homePage() {
        return "body/HomePage";
    }
	
}
