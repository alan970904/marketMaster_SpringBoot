package marketMaster.controller.product;

import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import marketMaster.DTO.restock.restock.RestockDetailsInsertDTO;
import marketMaster.DTO.restock.restock.RestockInsertDTO;
import marketMaster.service.product.ProductService;

@Component
@Aspect
public class ProductAOP {
	
	@Autowired
	private ProductService productService;

	@Pointcut("execution(* marketMaster.controller.restock.RestockInsertController.insertRestockData(..))")
	public void pointCutInsertRestock() {

	}

	@After("pointCutInsertRestock()")
	public void updateRestockNumber(JoinPoint joinpoint) {
		Object[] args = joinpoint.getArgs();
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
}
