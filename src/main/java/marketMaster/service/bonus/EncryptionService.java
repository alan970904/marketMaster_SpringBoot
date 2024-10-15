package marketMaster.service.bonus;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.stereotype.Service;
import java.util.logging.Logger;
import java.util.logging.Level;

@Service
public class EncryptionService {
	private static final Logger logger = Logger.getLogger(EncryptionService.class.getName());
    private static final String SECRET_KEY = "YourSecretKey123"; // 請使用更安全的方式儲存密鑰
    private static final String ALGORITHM = "AES";

    public String encrypt(String data) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            logger.info("Successfully encrypted data");
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
        	logger.log(Level.SEVERE, "Encryption failed", e);
            throw new RuntimeException("加密失敗", e);
        }
    }

    public String decrypt(String encryptedData) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            logger.info("Successfully decrypted data");
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
        	logger.log(Level.SEVERE, "Decryption failed", e);
            throw new RuntimeException("解密失敗", e);
        }
    }
}