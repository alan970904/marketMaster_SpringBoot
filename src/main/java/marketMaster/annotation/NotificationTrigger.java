package marketMaster.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * 標記需要觸發通知的方法。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotificationTrigger {
	// 定義事件名稱
	String event();
	// 定義角色列表,默認為空數組
    String[] roles() default {};
}
