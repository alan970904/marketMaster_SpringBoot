package marketMaster.controller.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import marketMaster.annotation.RequiresPermission;
import marketMaster.service.authorization.AuthorizationService;
import marketMaster.service.employee.EmployeeService;
import marketMaster.viewModel.EmployeeViewModel;

@Controller
public class ChangePasswordController {

    @Autowired
    private EmployeeService employeeService;
    
	@Autowired
	private AuthorizationService authService;

    @GetMapping("/employee/changePasswordPage")
    @RequiresPermission(value = "changePassword", resource = "employee")
    public String showChangePasswordPage(HttpSession session, Model model) {
        EmployeeViewModel currentEmployee = (EmployeeViewModel) session.getAttribute("employee");
        int authority = currentEmployee.getAuthority();
        
        boolean isFirstLogin = employeeService.isFirstLogin(currentEmployee.getEmployeeId());
        model.addAttribute("isFirstLogin", isFirstLogin);
        model.addAttribute("currentAuthority", authority);
        
        return "employee/ChangePassword";
    }

    @PostMapping("/employee/changePassword")
    @RequiresPermission(value = "changePassword", resource = "employee")
    public String changePassword(@RequestParam String newPassword, HttpSession session, Model model) {
        EmployeeViewModel currentEmployee = (EmployeeViewModel) session.getAttribute("employee");

        if (currentEmployee == null) {
            return "redirect:/employee/loginPage";
        }

        try {
            boolean success = employeeService.updatePassword(currentEmployee.getEmployeeId(), newPassword);
            if (success) {
                EmployeeViewModel updatedEmployee = employeeService.getEmployeeViewModel(currentEmployee.getEmployeeId());
                session.setAttribute("employee", updatedEmployee);
                return "redirect:/employee/loginPage";
            } else {
                model.addAttribute("errorMessage", "修改密碼失敗");
                return "employee/ChangePassword";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "修改密碼失敗: " + e.getMessage());
            return "employee/ChangePassword";
        }
    }
    
    @GetMapping("/employee/forgotPasswordPage")
    public String showForgotPasswordForm() {
        return "employee/ForgotPassword";
    }

    @PostMapping("/employee/forgotPassword")
    public String processForgotPassword(@RequestParam String employeeId, @RequestParam String idCardLast4, Model model) {
        boolean isResetSuccessful  = employeeService.resetPasswordAndSendEmail(employeeId, idCardLast4);
        if (isResetSuccessful ) {
            model.addAttribute("message", "密碼重置郵件已發送到您的電子郵箱。請檢查您的郵箱並使用臨時密碼登入。");
        } else {
            model.addAttribute("error", "員工編號或身份證號碼後四位不正確。");
        }
        return "employee/ForgotPassword";
    }
}
