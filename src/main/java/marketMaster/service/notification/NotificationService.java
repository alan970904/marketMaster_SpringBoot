package marketMaster.service.notification;

import java.util.List;

import marketMaster.bean.notification.Notification;

public interface NotificationService {

	void sendNotificationByEvent(String event, String[] roles);

	List<Notification> getLatestNotifications(String userId, int limit);

	void markAllAsRead(String userId);

}
