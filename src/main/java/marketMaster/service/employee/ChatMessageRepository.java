package marketMaster.service.employee;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import marketMaster.bean.employee.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
	@Query("SELECT cm FROM ChatMessage cm WHERE " +
	           "(cm.fromUser = :user1 AND cm.toUser = :user2) OR " +
	           "(cm.fromUser = :user2 AND cm.toUser = :user1) " +
	           "ORDER BY cm.timestamp")
	List<ChatMessage> findConversation(@Param("user1") String user1, @Param("user2") String user2, Pageable pageable);
}
