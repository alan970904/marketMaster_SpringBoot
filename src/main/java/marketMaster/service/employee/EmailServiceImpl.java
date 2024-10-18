package marketMaster.service.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSender mailSender;
	
    @Value("${spring.mail.username}")
    private String companyEmail;

    @Value("${company.name}")
    private String companyName;
	
	public void sendPasswordResetEmail(String to, String tempPassword) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(companyEmail);
		mailMessage.setTo(to);
		mailMessage.setSubject(companyName + " - 密碼重置");
		mailMessage.setText("您的臨時密碼是：" + tempPassword + "\n請使用此臨時密碼登入後立即修改您的密碼。");
		mailSender.send(mailMessage);
	}
}
