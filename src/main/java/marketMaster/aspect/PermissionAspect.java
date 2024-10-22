package marketMaster.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import marketMaster.annotation.RequiresPermission;
import marketMaster.service.authorization.AuthorizationServiceImpl;
import marketMaster.viewModel.EmployeeViewModel;

/*
 * 權限檢查
 * 主要用途是在執行需要權限的方法之前自動進行權限檢查，
 * 提供了一種集中管理和實施權限控制的方式。
 */

//這是一個切面類，用於實現權限檢查的橫切關注點
@Aspect
@Component
public class PermissionAspect {

	@Autowired
	private AuthorizationServiceImpl authorizationService;

	// 定義一個切點，當遇到被 @RequiresPermission 註解標記的方法時執行
	@Before("@annotation(requiresPermission") // 方法執行之前運行
	public void checkPermission(RequiresPermission requiresPermission) {
		// 獲取當前的 HTTP 請求
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		// 從 session 中獲取員工信息
		EmployeeViewModel employee = (EmployeeViewModel) request.getSession().getAttribute("employee");
	
		if (employee == null) {
			throw new SecurityException("未登錄用戶");
		}
		
		int userAuthority = employee.getAuthority();
		// 獲取所需的操作類型
		String operation = requiresPermission.value();
		// 獲取所需操作的資源
		String resource = requiresPermission.resource();
	
		if (!authorizationService.hasPermission(userAuthority, operation, resource)) {
			throw new SecurityException("權限不足");
		}
		
	}

}
