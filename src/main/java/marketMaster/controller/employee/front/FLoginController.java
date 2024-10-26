package marketMaster.controller.employee.front;

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
public class FLoginController {

	@Autowired
	private EmployeeService employeeService;
	
	@GetMapping("/front/loginPage")
	public String showLoginPage() {
		return "employee/front/FrontLogin";
	}
	
    @PostMapping("/front/login")
    public String login(@RequestParam String employeeId, @RequestParam String password, 
                        HttpSession session, Model model) {
        try {
            EmpBean employee = employeeService.login(employeeId, password);
            if (employee != null && employee.getResigndate() == null) {
                EmployeeViewModel employeeViewModel = employeeService.getEmployeeViewModel(employeeId);
                session.setAttribute("employee", employeeViewModel);

                // 添加權限級別的訊息
                int authority = employee.getAuthority();
                session.setAttribute("userAuthority", authority);
                
                if (employee.isFirstLogin()) {
                    return "redirect:/front/changePasswordPage";
                } else {
                    return "redirect:/mainPage";
                }
            } else {
                model.addAttribute("errorMessage", "員工編號或密碼錯誤");
                return "employee/front/FrontLogin";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "系統錯誤: " + e.getMessage());
            return "employee/front/FrontLogin";
        }
    }

    @GetMapping("/front/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/front/loginPage";
    }
    
    @GetMapping("/mainPage")
    public String homePage() {
        return "employeePage/MainPage";
    }

	
}
