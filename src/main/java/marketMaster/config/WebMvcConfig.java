package marketMaster.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import marketMaster.interceptor.LoginInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	private final LoginInterceptor loginInterceptor;

	public WebMvcConfig(LoginInterceptor loginInterceptor) {
		this.loginInterceptor = loginInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loginInterceptor)
        .addPathPatterns("/**")
        .excludePathPatterns("/employee/login", "/employee/loginPage", "/employee/logout", "/css/**", "/js/**", "/images/**");
	}
	
}
