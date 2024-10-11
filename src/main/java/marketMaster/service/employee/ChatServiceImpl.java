package marketMaster.service.employee;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import marketMaster.bean.employee.ChatMessage;

@Service
public class ChatServiceImpl implements ChateService {
	
	@Autowired
	private ChatMessageRepository chatMessageRepository;
	
	@Override
	@Transactional
	public void saveMessage(ChatMessage message) {
		chatMessageRepository.save(message);
	}
	
	@Override
	public List<ChatMessage> getRecentMessages(String user1, String user2, int limit) {
		return chatMessageRepository.findConversation(user1, user2, PageRequest.of(0, limit));
	}
}
