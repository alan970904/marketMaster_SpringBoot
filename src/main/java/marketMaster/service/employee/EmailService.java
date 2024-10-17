package marketMaster.service.employee;

public interface EmailService {
	
	void sendPasswordResetEmail(String to, String tempPassword);
}
