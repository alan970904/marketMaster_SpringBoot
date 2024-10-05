package marketMaster.exception;

import org.springframework.dao.DataAccessException;

public class EmpDataAccessException extends DataAccessException {

	private static final long serialVersionUID = 1L;

	public EmpDataAccessException(String message) {
		super(message);
	}
	
    public EmpDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

}
