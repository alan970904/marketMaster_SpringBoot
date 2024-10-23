package marketMaster.controller.product;

import java.util.Iterator;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import marketMaster.DTO.checkout.ReturnDetailDTO;
import marketMaster.DTO.checkout.ReturnProductDTO;
import marketMaster.DTO.product.ProductIdRestockNumDTO;
import marketMaster.DTO.restock.restock.RestockDetailDTO;
import marketMaster.DTO.restock.restock.RestockDetailsInsertDTO;
import marketMaster.DTO.restock.restock.RestockInsertDTO;
import marketMaster.bean.checkout.CheckoutDetailsBean;
import marketMaster.service.checkout.CheckoutDetailsService;
import marketMaster.service.product.ProductService;

@Component
@Aspect
public class ProductAOP {

	@Autowired
	private ProductService productService;
	
	@Autowired
    private CheckoutDetailsService checkoutDetailsService;

	ThreadLocal<ProductIdRestockNumDTO> beforeData = new ThreadLocal<>();

//	 進貨的切入點
	@Pointcut("execution(* marketMaster.controller.restock.RestockInsertController.insertRestockData(..))")
	public void pointCutInsertRestock() {
	}

	@Pointcut("execution(* marketMaster.controller.restock.RestockDetailController.updateRestockDetail(..))")
	public void pointCutUpdateRestock() {
	}

	@Pointcut("execution(* marketMaster.controller.restock.RestockDetailController.deleteByRestockDetailId(..))")
	public void pointCutDeleteRestock() {
	}
// 		結帳的切入點

	@Pointcut("execution(* marketMaster.service.checkout.CheckoutService.insertCheckoutWithDetails(..))")
	public void pointCutInsertCheckOut() {
	}
	@Pointcut("execution(* marketMaster.service.checkout.CheckoutDetailsService.updateCheckoutDetails(..))")
	public void pointCutUpdateCheckOut() {
	}
	@Pointcut("execution(* marketMaster.service.checkout.CheckoutDetailsService.getPartCheckoutDetails(..))")
	public void pointCutGetUpdateCheckOut() {
	}
	@Pointcut("execution(* marketMaster.service.checkout.CheckoutDetailsService.deleteCheckoutDetails(..))")
	public void pointCutDeleteCheckOut() {
	}
// 	退貨
	@Pointcut("execution(* marketMaster.service.checkout.ReturnProductService.addReturnProductWithDetails(..))")
	public void pointCutInsertReturn() {
		
	}
	
	@After("pointCutInsertRestock()")
	public void updateProductByInsertRestockNumber(JoinPoint joinpoint) {
		Object[] args = joinpoint.getArgs();
		if (args.length > 0 && args[0] instanceof List<?>) {
			List<?> restockInsertDTOList = (List<?>) args[0];
			for (Object object : restockInsertDTOList) {
				RestockInsertDTO RestockInsertDTO = (RestockInsertDTO) object;
				List<RestockDetailsInsertDTO> restockDetails = RestockInsertDTO.getRestockDetails();
				for (Object obj : restockDetails) {
					RestockDetailsInsertDTO restockDetail = (RestockDetailsInsertDTO) obj;
					String productId = restockDetail.getProductId();
					Integer numberOfRestock = restockDetail.getNumberOfRestock();
					productService.updateProductByInsertRestock(productId, numberOfRestock);
				}
			}
		}
	}

	@Before("pointCutUpdateRestock()")
	public void updateGetProductByUpdateRestockNumber(JoinPoint joinpoint) {
		Object[] args = joinpoint.getArgs();
		if (args.length > 0) {
			RestockDetailDTO restockDetailDTO = (RestockDetailDTO) args[0];
			String detailId = restockDetailDTO.getDetailId();
			ProductIdRestockNumDTO oldRestockNum = productService.findProductIdByRestockDetailId(detailId);
			beforeData.set(oldRestockNum);
		}
	}

	@After("pointCutUpdateRestock()")
	public void updateProductByUpdateRestockNumber(JoinPoint joinpoint) {
		Object[] args = joinpoint.getArgs();
		if (args.length > 0) {
			RestockDetailDTO restockDetailDTO = (RestockDetailDTO) args[0];
			ProductIdRestockNumDTO oldRestockData = beforeData.get();
			String productId = oldRestockData.getProductId();
			Integer oldNumberOfRestock = oldRestockData.getNumberOfRestock();
			Integer newNumberOfRestock = restockDetailDTO.getNumberOfRestock();
			productService.updateProductByUpdateRestock(productId, newNumberOfRestock, oldNumberOfRestock);
			
		}
		beforeData.remove();
	}
	
	@Before("pointCutDeleteRestock()")
	public void updateGetProductByDeleteRestockNumber(JoinPoint joinpoint) {
		Object[] args = joinpoint.getArgs();
		String detailId = (String) args[0];
		ProductIdRestockNumDTO oldRestockNum = productService.findProductIdByRestockDetailId(detailId);
		beforeData.set(oldRestockNum);
	}
	
	@After("pointCutDeleteRestock()")
	public void updateProductByDeleteRestockNumber() {
		ProductIdRestockNumDTO oldRestockData = beforeData.get();
		String productId = oldRestockData.getProductId();
		Integer numberOfRestock = oldRestockData.getNumberOfRestock();
		productService.updateProductByDeleteRestock(productId, numberOfRestock);
		System.out.println("刪除成功");
	}
	
	//結帳
	@After("pointCutInsertCheckOut()")
	public void updateProductByInsertCheckOut(JoinPoint joinpoint) {
		Object[] args = joinpoint.getArgs();
		List<CheckoutDetailsBean> checkoutDetails = (List<CheckoutDetailsBean>) args[1];
		for (CheckoutDetailsBean detail : checkoutDetails) {
			
			String productId = detail.getProductId();
			int numberOfCheckout = detail.getNumberOfCheckout();
			productService.updateProductByInsertCheckOut(productId, numberOfCheckout);
		}
	}
//	@After("pointCutUpdateCheckOut()")
//	public void updateProductByUpdateCheckOut(JoinPoint joinpoint) {
//		Object[] args = joinpoint.getArgs();
//		CheckoutDetailsBean detail = (CheckoutDetailsBean) args[0];
//		
//			
//			String productId = detail.getProductId();
//			int numberOfCheckout = detail.getNumberOfCheckout();
//			productService.updateProductByInsertCheckOut(productId, numberOfCheckout);
//		
//	}
//	
	@After("pointCutDeleteCheckOut()")
	public void updateProductByDeleteCheckOut(JoinPoint joinpoint) {
		Object[] args = joinpoint.getArgs();
		String checkoutId = (String) args[0];
		String productId = (String) args[1];
		CheckoutDetailsBean checkoutDetail = checkoutDetailsService.getCheckoutDetails(checkoutId, productId);
		int numberOfCheckout = checkoutDetail.getNumberOfCheckout();
		productService.updateProductByDeleteCheckOut(productId, numberOfCheckout);
		
		
		}
	
	//退貨
	@After("pointCutInsertReturn()")
	public void updateProductByInsertReturn(JoinPoint joinpoint) {
		Object[] args = joinpoint.getArgs();
		ReturnProductDTO returnProductDTO =  (ReturnProductDTO) args[0];
		List<ReturnDetailDTO> returnProducts = returnProductDTO.getReturnProducts();
		
		for (ReturnDetailDTO detail : returnProducts) {
			Integer numberOfReturn = detail.getNumberOfReturn();
			String productId = detail.getProductId();
			productService.updateProductByInsertReturn(productId, numberOfReturn);
		}
	}
	
}
