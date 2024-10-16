package marketMaster.service;

import org.springframework.stereotype.Service;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

	@Override
	public boolean hasPermission(int userAuthority, String operation, String resource) {

		switch (resource) {
		case "employee":
			return hasEmployeePermission(userAuthority, operation);
		case "askForLeave":
			return hasAskForLeavePermission(userAuthority, operation);
		case "schedule":
			return hasSchedulePermission(userAuthority, operation);
		case "products":
			return hasProductsPermission(userAuthority, operation);
		case "restock":
			return hasRestockPermission(userAuthority, operation);
		case "supplier":
			return hasSupplierPermission(userAuthority, operation);
		case "checkout":
			return hasCheckoutPermission(userAuthority, operation);
		case "return":
			return hasReturnPermission(userAuthority, operation);
		case "customer":
			return hasCustomerPermission(userAuthority, operation);
		case "bonus":
			return hasBonusPermission(userAuthority, operation);
		default:
			return false;
		}
	}


	private boolean hasBonusPermission(int userAuthority, String operation) {
		return false;
	}

	private boolean hasCustomerPermission(int userAuthority, String operation) {
		return false;
	}
	
	private boolean hasReturnPermission(int userAuthority, String operation) {
		return false;
	}

	private boolean hasCheckoutPermission(int userAuthority, String operation) {
		return false;
	}

	private boolean hasSupplierPermission(int userAuthority, String operation) {
		return false;
	}

	private boolean hasRestockPermission(int userAuthority, String operation) {
		return false;
	}

	private boolean hasProductsPermission(int userAuthority, String operation) {
		return false;
	}

	private boolean hasSchedulePermission(int userAuthority, String operation) {
		return false;
	}

	private boolean hasAskForLeavePermission(int userAuthority, String operation) {
		return false;
	}

	private boolean hasEmployeePermission(int userAuthority, String operation) {
		switch(operation) {
		case "view":
		case "viewList":
		case "search":
		case "update":
		case "chagePassword":
		case "forgotPassword":
			return userAuthority >= 1; // 限制權限1
		case "viewDetails":
		case "add":
		case "delete":
		case "updateAll":
			return userAuthority >= 2; // 權限2、3對所有員工都能操作，但權限2不能更新經理
		default:
			return false;
			
		}
	}
}
