package marketMaster.service.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import marketMaster.bean.notification.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

}
