package marketMaster.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import marketMaster.requiresPermission.RequiresPermission;
import marketMaster.service.authorization.AuthorizationServiceImpl;
import marketMaster.viewModel.EmployeeViewModel;

@Aspect
@Component
public class PermissionAspect {

	@Autowired
	private AuthorizationServiceImpl authorizationService;

	@Before("@annotation(requiresPermission")
	public void checkPermission(RequiresPermission requiresPermission) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		EmployeeViewModel employee = (EmployeeViewModel) request.getSession().getAttribute("employee");
	
		if (employee == null) {
			throw new SecurityException("未登錄用戶");
		}
		
		int userAuthority = employee.getAuthority();
		String operation = requiresPermission.value();
		String resource = requiresPermission.resource();
	
		if (!authorizationService.hasPermission(userAuthority, operation, resource)) {
			throw new SecurityException("權限不足");
		}
		
	}

}
