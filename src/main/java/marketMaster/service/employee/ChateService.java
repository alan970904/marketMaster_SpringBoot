package marketMaster.service.employee;

import java.util.List;

import marketMaster.bean.employee.ChatMessage;

public interface ChateService {

	void saveMessage(ChatMessage message);

	List<ChatMessage> getRecentMessages(String user1, String user2, int limit);

}
