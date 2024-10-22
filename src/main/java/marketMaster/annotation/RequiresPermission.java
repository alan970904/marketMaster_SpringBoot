package marketMaster.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * 主要用途是標記那些需要進行權限檢查的方法。
 * 當這個方法被調用時，PermissionAspect 就會進行相應的權限檢查。
 */

//自定義註解，用於標記需要權限檢查的方法
@Target(ElementType.METHOD) // 這個註解只能用於方法
@Retention(RetentionPolicy.RUNTIME) // 這個註解在運行時可以被讀取
public @interface RequiresPermission {
	// 定義操作類型
	String value();
	// 定義資源類型
	String resource();
	
}
