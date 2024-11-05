package marketMaster.controller.employee.front;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import marketMaster.bean.employee.ChatMessage;
import marketMaster.bean.employee.EmpBean;
import marketMaster.service.employee.ChatServiceImpl;
import marketMaster.service.employee.EmployeeService;
import marketMaster.viewModel.EmployeeViewModel;

@Controller
@RequestMapping("/front/chat")
public class FChatController {
	
	@Autowired
	private ChatServiceImpl chatService;
	
	@Autowired
	private EmployeeService employeeService;
	
	@GetMapping("/messages")
	public String showChatMessages(Model model, HttpSession session) {
	    EmployeeViewModel currentEmployee = (EmployeeViewModel) session.getAttribute("frontendEmployee");
	    if (currentEmployee == null) {
	        return "redirect:/front/loginPage";
	    }
	    
	    List<EmpBean> allUsers = employeeService.findAllEmp(); // 獲取所有用戶
	    
	    // 過濾掉當前用戶
	    List<EmpBean> otherUsers = allUsers.stream()
	        .filter(user -> !user.getEmployeeId().equals(currentEmployee.getEmployeeId()))
	        .collect(Collectors.toList());
	    
	    model.addAttribute("onlineUsers", otherUsers);
	    model.addAttribute("currentEmployee", currentEmployee);
	    
	    return "employee/front/FrontChatMessages";
	}
	
	@PostMapping("/send")
	public ResponseEntity<?> sendMessage(@RequestBody ChatMessage message, HttpSession session) {
		EmployeeViewModel employee = (EmployeeViewModel) session.getAttribute("frontendEmployee");
		
		if (employee == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }
		
		message.setFromUser(employee.getEmployeeId());
	    chatService.saveMessage(message);
	    return ResponseEntity.ok().build();
	}
	
	@GetMapping("/history")
	@ResponseBody
	public ResponseEntity<List<ChatMessage>> getMessageHistory(@RequestParam String otherUser, HttpSession session) {
	    try {
	        EmployeeViewModel employee = (EmployeeViewModel) session.getAttribute("frontendEmployee");
	        
	        if (employee == null) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	        }
	        
	        List<ChatMessage> messages = chatService.getRecentMessages(employee.getEmployeeId(), otherUser, 50);
	        return ResponseEntity.ok(messages != null ? messages : new ArrayList<>());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}
	
}
