package marketMaster.service.notification;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import marketMaster.bean.notification.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

	@Query(value = "SELECT * FROM notification n WHERE n.employee_id = :userId ORDER BY n.created_at DESC", nativeQuery = true)
	List<Notification> findTopNByEmployeeIdOrderByCreatedAtDesc(@Param("userId") String userId);

	@Modifying
	@Query("UPDATE Notification n SET n.isRead = true WHERE n.employee.employeeId = :userId AND n.isRead = false")
	void markAllAsReadForUser(@Param("userId") String userId);

}
