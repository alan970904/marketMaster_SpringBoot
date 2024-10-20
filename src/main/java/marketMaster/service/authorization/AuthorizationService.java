package marketMaster.service.authorization;

//定義授權服務的接口
public interface AuthorizationService {
	// 檢查用戶是否有權限執行特定操作(用戶的權限級別、要執行的操作、操作的資源)
	boolean hasPermission(int userAuthority, String operation, String resource);

}
