package marketMaster.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmpDataAccessException.class)
    public String handleDataAccessException(EmpDataAccessException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/dataAccessError";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException ex, Model model) {
        model.addAttribute("errorMessage", "文件太大，無法上傳");
        return "error/fileUploadError";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        model.addAttribute("errorMessage", "發生未知錯誤：" + ex.getMessage());
        return "error/generalError";
    }
}
