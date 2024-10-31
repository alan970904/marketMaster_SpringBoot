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
	
	@GetMapping("/employee/loginPage")
	public String showLoginPage() {
		return "employee/Login";
	}
	
    @PostMapping("/employee/login")
    public String login(@RequestParam String employeeId, @RequestParam String password, 
                        HttpSession session, Model model) {
        try {
            EmpBean employee = employeeService.login(employeeId, password);
            if (employee != null && employee.getResigndate() == null) {
                EmployeeViewModel employeeViewModel = employeeService.getEmployeeViewModel(employeeId);
                session.setAttribute("backendEmployee", employeeViewModel);

                // 添加權限級別的訊息
                int authority = employee.getAuthority();
                session.setAttribute("backendAuthority", authority);
                
                if (employee.isFirstLogin()) {
                    return "redirect:/employee/changePasswordPage";
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

    @GetMapping("/employee/logout")
    public String logout(HttpSession session) {
        // 只清除後台相關的 session 屬性
        session.removeAttribute("backendEmployee");
        session.removeAttribute("backendAuthority");
        return "redirect:/employee/loginPage";
    }
    
    @GetMapping("/homePage")
    public String homePage(HttpSession session, Model model) {
        model.addAttribute("employee", session.getAttribute("backendEmployee"));
    	return "body/HomePage";
    }
	
}
