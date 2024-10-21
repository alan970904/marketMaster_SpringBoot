package marketMaster.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

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
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private ChatServiceImpl chatService;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String userId = getUserId(session);
		sessions.put(userId, session);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String payload = message.getPayload();
		// 使用 ObjectMapper 進行 JSON 和 Java 對象之間的轉換。
		ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);

		// 保存消息到數據庫
		chatService.saveMessage(chatMessage);

		// 發送消息給接收者
		WebSocketSession recipientSession = sessions.get(chatMessage.getToUser());
		if (recipientSession != null && recipientSession.isOpen()) {
			recipientSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		String userId = getUserId(session);
		sessions.remove(userId);
	}

	private String getUserId(WebSocketSession session) {
		// 從 session 中獲取用戶 ID
		return (String) session.getAttributes().get("userId");
	}

}
