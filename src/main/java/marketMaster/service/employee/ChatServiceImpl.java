package marketMaster.service.employee;

import java.util.List;

import org.hibernate.Hibernate;
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
    @Transactional
    public List<ChatMessage> getRecentMessages(String user1, String user2, int limit) {
        List<ChatMessage> messages = chatMessageRepository.findConversation(user1, user2, PageRequest.of(0, limit));
        messages.forEach(message -> {
            Hibernate.initialize(message.getFromEmployee());
            Hibernate.initialize(message.getToEmployee());
        });
        return messages;
    }
}
