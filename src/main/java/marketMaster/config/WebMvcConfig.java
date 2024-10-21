package marketMaster.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import marketMaster.interceptor.LoginInterceptor;
import marketMaster.interceptor.front.FrontLoginInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	private String uploadDir;
	
	@Autowired
	private LoginInterceptor loginInterceptor;
	
	@Autowired
	private FrontLoginInterceptor frontLoginInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(loginInterceptor)
        .addPathPatterns("/employee/**", "/homePage")
        .excludePathPatterns("/employee/login", "/employee/loginPage", 
        					"/employee/logout", "/employee/forgotPasswordPage", 
        					"/employee/forgotPassword", "/css/**", "/js/**", "/images/**", 
        					"/uploads/**", "/supplier/supplier2");
	
		registry.addInterceptor(frontLoginInterceptor)
		.addPathPatterns("/front/**", "/mainPage")
		.excludePathPatterns("/front/login", "/front/loginPage", 
							"/front/logout", "/front/forgotPasswordPage", 
							"/front/forgotPassword", "/css/**", "/js/**", "/images/**", 
							"/uploads/**");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:" + uploadDir + "/");

	}
	
}
