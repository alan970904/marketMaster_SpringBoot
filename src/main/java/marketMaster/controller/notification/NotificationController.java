package marketMaster.controller.notification;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import marketMaster.bean.notification.Notification;
import marketMaster.service.notification.NotificationServiceImpl;
import marketMaster.viewModel.EmployeeViewModel;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

	@Autowired
	private NotificationServiceImpl notificationService;

	@GetMapping("/latest")
	public ResponseEntity<?> getLatestNotifications(HttpSession session) {
		EmployeeViewModel currentEmployee = (EmployeeViewModel) session.getAttribute("employee");
		
		if (currentEmployee != null) {
			// 從 session 中獲取當前用戶 ID
			List<Notification> notifications = notificationService.getLatestNotifications(currentEmployee.getEmployeeId(), 5);
			return ResponseEntity.ok(notifications);
		}
		
		return ResponseEntity.badRequest().body("No employee in session");
	}

	@PostMapping("/markAsRead")
	public ResponseEntity<?> markNotificationsAsRead(HttpSession session) {
		EmployeeViewModel currentEmployee = (EmployeeViewModel) session.getAttribute("employee");
		
		if (currentEmployee != null) {
			notificationService.markAllAsRead(currentEmployee.getEmployeeId());
			return ResponseEntity.ok().build();
		}
		
		return ResponseEntity.badRequest().body("No employee in session");
	}
	
}
