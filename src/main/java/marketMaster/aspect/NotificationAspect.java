package marketMaster.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import marketMaster.annotation.NotificationTrigger;
import marketMaster.service.notification.NotificationService;

@Aspect
@Component
public class NotificationAspect {

	@Autowired
	private NotificationService notificationService;
	
	@AfterReturning("@annotation(sendNotification)")
	public void sendNotification(NotificationTrigger notificationTrigger) {
		String event = notificationTrigger.event();
        String[] roles = notificationTrigger.roles();
		
		notificationService.sendNotificationByEvent(event, roles);
	}
}
