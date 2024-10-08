package marketMaster.controller.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import marketMaster.service.employee.EmployeeService;
import marketMaster.viewModel.EmployeeViewModel;

@Controller
public class ChangePasswordController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/employee/changePasswordPage")
    public String showChangePasswordPage(HttpSession session, Model model) {
        EmployeeViewModel employee = (EmployeeViewModel) session.getAttribute("employee");
        if (employee == null) {
            return "redirect:/employee/loginPage";
        }
        
        boolean isFirstLogin = employeeService.isFirstLogin(employee.getEmployeeId());
        model.addAttribute("isFirstLogin", isFirstLogin);
        
        return "employee/ChangePassword";
    }

    @PostMapping("/employee/changePassword")
    public String changePassword(@RequestParam String newPassword, HttpSession session, Model model) {
        EmployeeViewModel employee = (EmployeeViewModel) session.getAttribute("employee");

        if (employee == null) {
            return "redirect:/employee/loginPage";
        }

        try {
            boolean success = employeeService.updatePassword(employee.getEmployeeId(), newPassword);
            if (success) {
                EmployeeViewModel updatedEmployee = employeeService.getEmployeeViewModel(employee.getEmployeeId());
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
        boolean isValid = employeeService.validateEmployeeInfo(employeeId, idCardLast4);
        if (isValid) {
            // 生成臨時密碼
            String tempPassword = employeeService.generateTempPassword();
            employeeService.updatePassword(employeeId, tempPassword);
            model.addAttribute("message", "臨時密碼已生成，請使用臨時密碼登入後立即修改密碼。");
            model.addAttribute("tempPassword", tempPassword);
        } else {
            model.addAttribute("error", "員工編號或身份證號碼後四位不正確。");
        }
        return "employee/ForgotPassword";
    }
}
