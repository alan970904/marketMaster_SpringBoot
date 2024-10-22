package marketMaster.service.authorization;

import org.springframework.stereotype.Service;

/*
 * 權限邏輯
 * 主要用途是提供細粒度的權限控制，
 * 可以針對不同的資源和操作設置不同的權限要求。
 */

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

	@Override
	public boolean hasPermission(int userAuthority, String operation, String resource) {
		// 根據資源類型選擇相應的權限檢查方法
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


	// 權限對紅利的操作
	private boolean hasBonusPermission(int userAuthority, String operation) {
		return false;
	}

	// 權限對會員的操作
	private boolean hasCustomerPermission(int userAuthority, String operation) {
		switch(operation) {
		case "view":
		case "viewList":
		case "search":
		case "update":
		case "viewDetails":
		case "add":
		case "updateAll":
			return userAuthority >= 1; // 權限級別 1 或以上可以執行這些操作
		case "delete":
			return userAuthority >= 2; // 權限級別 2 或以上可以刪除
		default:
			return false;
			
		}
	}
	
	// 權限對退貨的操作
	private boolean hasReturnPermission(int userAuthority, String operation) {
		return false;
	}

	// 權限對結帳的操作
	private boolean hasCheckoutPermission(int userAuthority, String operation) {
		return false;
	}

	// 權限對供應商的操作
	private boolean hasSupplierPermission(int userAuthority, String operation) {
		return false;
	}

	// 權限對進貨的操作
	private boolean hasRestockPermission(int userAuthority, String operation) {
		return false;
	}

	// 權限對商品的操作
	private boolean hasProductsPermission(int userAuthority, String operation) {
		return false;
	}

	// 權限對排班表的操作
	private boolean hasSchedulePermission(int userAuthority, String operation) {
		return false;
	}

	// 權限對請假的操作
	private boolean hasAskForLeavePermission(int userAuthority, String operation) {
		return false;
	}

	// 權限對員工的操作
	private boolean hasEmployeePermission(int userAuthority, String operation) {
		switch(operation) {
		case "view":
		case "viewList":
		case "search":
		case "update":
		case "chagePassword":
		case "forgotPassword":
			return userAuthority >= 1; // 權限級別 1 或以上可以執行這些操作
		case "add":
		case "delete":
			return userAuthority >= 2; // 權限級別 2 或以上可以添加和刪除員工
		default:
			return false;
			
		}
	}
}
