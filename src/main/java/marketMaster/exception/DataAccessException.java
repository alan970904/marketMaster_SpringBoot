package marketMaster.exception;

//DataAccessException 類：自定義的數據訪問異常
@SuppressWarnings("serial")
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}