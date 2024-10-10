package marketMaster.service;

public interface AuthorizationService {

	boolean hasPermission(int userAuthority, String operation, String resource);

}