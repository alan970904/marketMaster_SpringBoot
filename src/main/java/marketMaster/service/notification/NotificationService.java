package marketMaster.service.notification;

public interface NotificationService {

	void sendNotificationByEvent(String event, String[] roles);

}
