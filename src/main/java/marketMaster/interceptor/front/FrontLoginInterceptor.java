package marketMaster.interceptor.front;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class FrontLoginInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession();
		
		// 檢查 session 中是否存在 employee 屬性
		if(session.getAttribute("frontendEmployee") == null) {
			// 獲取當前請求的 URL
			String requestURI = request.getRequestURI();
			
			// 如果當前請求不是登入頁面或登入處理的 URL，則重定向到登入頁面
			if (!requestURI.contains("/front/login") && !requestURI.contains("/front/loginPage")) {
				response.sendRedirect(request.getContextPath() + "/front/loginPage");
				return false;
			}
		}
		return true;
	}

}
