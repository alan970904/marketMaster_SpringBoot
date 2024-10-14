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

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

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
		// 從 session 中獲取用戶 ID，這裡假設您在建立 WebSocket 連接時將用戶 ID 存儲在了 session 屬性中
		return (String) session.getAttributes().get("userId");
	}

}
