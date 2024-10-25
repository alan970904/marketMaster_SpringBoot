package marketMaster.handler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import marketMaster.bean.employee.ChatMessage;
import marketMaster.service.employee.ChatServiceImpl;

/*
 * 負責維護 WebSocket 連接的生命週期,
 * 處理incoming消息,並將消息轉發給適當的接收者。
 */

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

	// 用於存儲所有活動的WebSocket會話
	private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
	private final ObjectMapper objectMapper;

	@Autowired
	private ChatServiceImpl chatService;

    public ChatWebSocketHandler() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// 從session中獲取員工ID
		String employeeId  = extractEmployeeId(session);
		if (employeeId  != null) {
			sessions.put(employeeId , session);
		}
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String payload = message.getPayload();
		// 使用 ObjectMapper 進行 JSON 和 Java 對象之間的轉換。
		ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);

        // 如果timestamp為null，設置當前時間
        if (chatMessage.getTimestamp() == null) {
            chatMessage.setTimestamp(LocalDateTime.now());
        }
		
		// 保存消息到數據庫
		chatService.saveMessage(chatMessage);
		
		String messageJson = objectMapper.writeValueAsString(chatMessage);

		// 發送消息給接收者
		WebSocketSession recipientSession = sessions.get(chatMessage.getToUser());
		if (recipientSession != null && recipientSession.isOpen()) {
			recipientSession.sendMessage(new TextMessage(messageJson));
		}
		
		// 發送確認消息給發送者
		WebSocketSession senderSession = sessions.get(chatMessage.getFromUser());
		if (senderSession != null && senderSession.isOpen()) {
			senderSession.sendMessage(new TextMessage(messageJson));
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		String employeeId  = extractEmployeeId(session);
		if (employeeId  != null) {
			sessions.remove(employeeId );
		}
	}

	private String extractEmployeeId(WebSocketSession session) {
		Map<String, Object> attributes = session.getAttributes();
		return attributes.get("employeeId") != null ? attributes.get("employeeId").toString() : null;
	}

}
