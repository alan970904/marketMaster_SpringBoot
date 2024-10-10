package marketMaster.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ErrorController {
	
	@GetMapping("/unauthorized")
	public String unauthorizedAccess(@RequestParam(required = false) String message, Model model) {
        if (message == null || message.isEmpty()) {
            message = "權限不足，無法操作";
        }
        model.addAttribute("errorMessage", message);
        return "error/unauthorized";
	}
	
}
