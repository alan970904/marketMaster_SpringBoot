package marketMaster.controller.employee;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import marketMaster.bean.employee.ChatMessage;
import marketMaster.service.employee.ChatServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
	
	@Autowired
	private ChatServiceImpl chatService;
	
	@PostMapping("/send")
	public ResponseEntity<?> sendMessage(@RequestBody ChatMessage message, @AuthenticationPrincipal UserDetails userDetails) {
		message.setFromUser(userDetails.getUsername());
		chatService.saveMessage(message);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/history")
	public ResponseEntity<List<ChatMessage>> getMessageHistory(@RequestParam String otherUser, @AuthenticationPrincipal UserDetails userDetails) {
		List<ChatMessage> messages = chatService.getRecentMessages(userDetails.getUsername(), otherUser, 50);
		return ResponseEntity.ok(messages);
	}
	
}
