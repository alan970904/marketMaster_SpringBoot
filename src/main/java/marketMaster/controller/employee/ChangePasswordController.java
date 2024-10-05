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
    public String showChangePasswordPage() {
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
}
