package marketMaster.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/*
 * 加密配置類，
 * 使用 Spring Security 提供的 BCryptPasswordEncoder 來進行密碼加密
 */
@Configuration
public class PasswordConfig {

	/*
	 *  讓 Spring 容器管理 PasswordEncoder 實例，
	 *  其他元件可以透過依賴注入來使用這個加密器
	 */
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
