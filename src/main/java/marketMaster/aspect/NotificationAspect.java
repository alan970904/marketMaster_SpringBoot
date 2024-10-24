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

	// 定義切點,在帶有NotificationTrigger註解的方法執行後觸發
	@AfterReturning("@annotation(sendNotification)")
	public void sendNotification(NotificationTrigger notificationTrigger) {
		String event = notificationTrigger.event(); // 獲取事件名稱
		String[] roles = notificationTrigger.roles(); // 獲取角色列表

		notificationService.sendNotificationByEvent(event, roles); // 調用服務發送通知
	}
}