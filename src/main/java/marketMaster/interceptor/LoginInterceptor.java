package marketMaster.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// 如果 "employee" 屬性不存在（即用戶未登入），且當前請求的 URL 不是登入頁面或登入處理的 URL，則重定向到登入頁面。
// 如果用戶已登入或正在訪問登入相關的頁面，則允許請求繼續處理。
@Component
public class LoginInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession();
		
		// 檢查 session 中是否存在 employee 屬性
		if(session.getAttribute("employee") == null) {
			// 獲取當前請求的 URL
			String requestURI = request.getRequestURI();
			
			// 如果當前請求不是登入頁面或登入處理的 URL，則重定向到登入頁面
			if (!requestURI.contains("/employee/login") && !requestURI.contains("/employee/loginPage")) {
				response.sendRedirect(request.getContextPath() + "/employee/loginPage");
				return false;
			}
		}
		return true;
	}

}
