package marketMaster.exception;

import java.io.Serializable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


//紅利商品管理相關的異常類集合
public class BonusException {

    //紅利商品管理基礎異常
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class ItemManagementException extends RuntimeException implements Serializable {
        private static final long serialVersionUID = 1L;

        public ItemManagementException(String message) {
            super(message);
        }

        public ItemManagementException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    //重複商品異常
    @ResponseStatus(HttpStatus.CONFLICT)
    public static class DuplicateItemException extends ItemManagementException implements Serializable {
        private static final long serialVersionUID = 1L;

        public DuplicateItemException(String message) {
            super(message);
        }
    }

    //無效商品數據異常
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidItemDataException extends ItemManagementException implements Serializable {
        private static final long serialVersionUID = 1L;

        public InvalidItemDataException(String message) {
            super(message);
        }
    }

    //ID生成異常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public static class IdGenerationException extends ItemManagementException implements Serializable {
        private static final long serialVersionUID = 1L;

        public IdGenerationException(String message) {
            super(message);
        }

        public IdGenerationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // 私有構造函數，防止實例化
    private BonusException() {
        throw new AssertionError("不應該實例化 BonusException");
    }
}