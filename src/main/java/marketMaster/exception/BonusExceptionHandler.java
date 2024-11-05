package marketMaster.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//紅利商品管理模組的異常處理器
@RestControllerAdvice(basePackages = "marketMaster.controller.bonus")
public class BonusExceptionHandler {

    //處理商品管理基礎異常
    @ExceptionHandler(BonusException.ItemManagementException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleItemManagementException(
            BonusException.ItemManagementException ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    //處理重複商品異常
    @ExceptionHandler(BonusException.DuplicateItemException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleDuplicateItemException(
            BonusException.DuplicateItemException ex) {
        return createErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    //處理無效商品數據異常
    @ExceptionHandler(BonusException.InvalidItemDataException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleInvalidItemDataException(
            BonusException.InvalidItemDataException ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    //處理ID生成異常
    @ExceptionHandler(BonusException.IdGenerationException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleIdGenerationException(
            BonusException.IdGenerationException ex) {
        return createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "生成商品編號失敗：" + ex.getMessage()
        );
    }

    //處理參數驗證失敗異常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        String errorMessage = fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return createErrorResponse(HttpStatus.BAD_REQUEST, "資料驗證失敗：" + errorMessage);
    }

     //處理參數類型轉換異常
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        String errorMessage = String.format(
                "參數'%s'類型轉換失敗，應為: %s",
                ex.getName(),
                ex.getRequiredType().getSimpleName()
        );
        return createErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

     //處理所有未捕獲的異常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        return createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "處理請求時發生錯誤：" + ex.getMessage()
        );
    }

     //創建統一的錯誤響應格式
    private ResponseEntity<Map<String, Object>> createErrorResponse(
            HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("status", status.value());
        body.put("message", message);
        body.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(status).body(body);
    }
}