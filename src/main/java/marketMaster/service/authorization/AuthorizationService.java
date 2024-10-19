package marketMaster.service.authorization;

public interface AuthorizationService {

	boolean hasPermission(int userAuthority, String operation, String resource);

}