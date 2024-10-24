package marketMaster.service.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import marketMaster.bean.employee.EmpBean;
import marketMaster.bean.notification.Notification;
import marketMaster.service.employee.EmployeeRepository;

import java.util.Arrays;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
	public void sendNotificationByEvent(String event, String[] roles) {
        String message = getMessageByEvent(event);
        List<EmpBean> employees = employeeRepository.findByRankLevel_PositionNameIn(Arrays.asList(roles));

        for (EmpBean employee : employees) {
            Notification notification = new Notification(employee, "SYSTEM", message);
            notificationRepository.save(notification);
        }
    }

    private String getMessageByEvent(String event) {
        switch (event) {
            case "NEW_EMPLOYEE":
                return "新員工已加入公司";
            case "EMPLOYEE_RESIGNED":
                return "有員工已離職";
            default:
                return "系統通知";
        }
    }

	@Override
	public List<Notification> getLatestNotifications(String userId, int limit) {
		List<Notification> allNotifications = notificationRepository.findTopNByEmployeeIdOrderByCreatedAtDesc(userId);
		return allNotifications.subList(0, Math.min(limit, allNotifications.size()));
	}

	@Override
	@Transactional
	public void markAllAsRead(String userId) {
		notificationRepository.markAllAsReadForUser(userId);
	}
}
