package marketMaster.controller.product;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class ProductAOP {
	
	@Pointcut("execution(* marketMaster.controller.restock.RestockInsertController.insertRestockData(..))")
	public void pointCutRestock() {
		
	}
	
	@Before("pointCutRestock()")
	public void getInfo(JoinPoint joinpoint) {
//		Object this1 = joinpoint.getArgs();
		System.out.println("======before======");
//		System.out.println(this1);
	}
	
	@After("pointCutRestock()")
	public void test1(JoinPoint joinpoint) {
		
		System.out.println("======攔截成功======");
	
	}
}
