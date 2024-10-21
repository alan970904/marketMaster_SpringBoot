package marketMaster.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import marketMaster.interceptor.LoginInterceptor;

@Configuration
@ConfigurationProperties(prefix = "app")
public class WebMvcConfig implements WebMvcConfigurer {

	private String uploadDir;

	@Autowired
	private LoginInterceptor loginInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(loginInterceptor)
				.addPathPatterns("/**")
				.excludePathPatterns(
						"/employee/login",
						"/employee/loginPage",
						"/employee/logout",
						"/employee/forgotPasswordPage",
						"/employee/forgotPassword",
						"/css/**",
						"/js/**",
						"/images/**",
						"/marketMaster/supplier/supplier",
						"/supplier/supplier",
						"/marketMaster/supplier/ecpayReturn",
						"/supplier/ecpayReturn"
				);	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
		registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
		registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");
		registry.addResourceHandler("/uploads/**").addResourceLocations("file:" + uploadDir + "/");

	}

	public void setUploadDir(String uploadDir) {
		this.uploadDir = uploadDir;
	}

}