package marketMaster.controller.product;

import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import marketMaster.DTO.restock.restock.RestockDetailDTO;
import marketMaster.DTO.restock.restock.RestockDetailsInsertDTO;
import marketMaster.DTO.restock.restock.RestockInsertDTO;
import marketMaster.service.product.ProductService;

@Component
@Aspect
public class ProductAOP {
	
	@Autowired
	private ProductService productService;

	// 新增的切入點
	@Pointcut("execution(* marketMaster.controller.restock.RestockInsertController.insertRestockData(..))")
	public void pointCutInsertRestock() {
	}
	@Pointcut("execution(* marketMaster.controller.restock.RestockDetailController.updateRestockDetail(..))")
	public void pointCutUpdateRestock() {
	}
	@Pointcut("execution(* marketMaster.controller.restock.RestockDetailController.deleteByRestockDetailId(..))")
	public void pointCutDeleteRestock() {
	}

	@After("pointCutInsertRestock()")
	public void updateProductByInsertRestockNumber(JoinPoint joinpoint) {
		Object[] args = joinpoint.getArgs();
		System.out.println("攔截成功");
		if (args.length > 0 && args[0] instanceof List<?>) {
			List<?> restockInsertDTOList  = (List<?>) args[0] ;
			for (Object object : restockInsertDTOList) {
					RestockInsertDTO RestockInsertDTO = (RestockInsertDTO) object;
					List<RestockDetailsInsertDTO> restockDetails = RestockInsertDTO.getRestockDetails();
					for (Object obj : restockDetails) {
						RestockDetailsInsertDTO restockDetail =  (RestockDetailsInsertDTO) obj;
						String productId = restockDetail.getProductId();
						Integer numberOfRestock = restockDetail.getNumberOfRestock();
						productService.updateRestockProduct(productId, numberOfRestock);
					}
			}
		}
	}
	
	@After("pointCutUpdateRestock()")
	public void updateProductByUpdateRestockNumber(JoinPoint joinpoint) {
		Object[] args = joinpoint.getArgs();
		System.out.println("攔截成功");
		System.out.println(args);
		System.out.println(args.length);
		if (args.length > 0) {
			System.out.println("if內");
			List<?> RestockDetailDTO  = (List<?>) args[0] ;
			for (Object object : RestockDetailDTO) {
				RestockDetailDTO aRestockDetailDTO = (RestockDetailDTO) object;
				System.out.println("===================");
				System.out.println(aRestockDetailDTO.getProductId());
				System.out.println(aRestockDetailDTO.getNumberOfRestock());
				
			}
		}
		System.out.println("if外");
	}
}
