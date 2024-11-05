package marketMaster.service.product;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import marketMaster.DTO.product.InventoryCheckDetailDTO;
import marketMaster.DTO.product.InventoryCheckInsertDTO;
import marketMaster.bean.employee.EmpBean;
import marketMaster.bean.product.InventoryCheckBean;
import marketMaster.bean.product.InventoryCheckDetailsBean;
import marketMaster.bean.product.ProductBean;
import marketMaster.service.employee.EmployeeRepository;

@Service
public class InventoryCheckService {

	@Autowired
	private InventoryCheckRepository inventoryCheckRepo;

	@Autowired
	private InventoryCheckDetailsService inventoryCheckDetailsService;

	@Autowired
	private InventoryCheckDetailsRepository inventoryCheckDetailsRepo;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private ProductService productService;

	public InventoryCheckBean findDetailsBycheckId(String inventoryCheckId) {
		Optional<InventoryCheckBean> optional = inventoryCheckRepo.findById(inventoryCheckId);
		if (optional.isPresent()) {
			InventoryCheckBean inventoryCheck = optional.get();
			return inventoryCheck;
		}
		return null;
	}

	public List<InventoryCheckBean> findAllInventoryCheck() {
		List<InventoryCheckBean> inventoryChecks = inventoryCheckRepo.findAll();

		return inventoryChecks;
	}

	public ResponseEntity<?> addInventoryCheck(InventoryCheckInsertDTO inventoryCheckInsertDTO) {
		List<InventoryCheckDetailDTO> detailsList = inventoryCheckInsertDTO.getDetails();
		List<Map<String, Object>> existingProducts = new ArrayList<>();
		List<InventoryCheckDetailDTO> validDetails = new ArrayList<>();

		for (InventoryCheckDetailDTO detail : detailsList) {
			boolean hasNoVerify = inventoryCheckDetailsRepo.findVerifyByProductId(detail.getProductId(), false);

			if (hasNoVerify) {
				ProductBean product = productService.findOneProduct(detail.getProductId());
				existingProducts.add(Map.of("productId", product.getProductId(), "productName",
						product.getProductName(), "message", "此商品已在盤點單內"));

			} else {
				validDetails.add(detail);
			}

		}

		LocalDate now = LocalDate.now();
		InventoryCheckBean inventoryCheck = new InventoryCheckBean();

		Optional<EmpBean> optional = employeeRepository.findById(inventoryCheckInsertDTO.getEmployeeId());
		inventoryCheck.setInventoryCheckId(newCheckId());
		inventoryCheck.setEmployee(optional.get());
		inventoryCheck.setInventoryCheckDate(now);
		inventoryCheck.setVerifyStatus(false);

		inventoryCheckRepo.save(inventoryCheck);

		for (InventoryCheckDetailDTO detail : validDetails) {
			InventoryCheckDetailsBean inventoryCheckDetail = new InventoryCheckDetailsBean();

			ProductBean product = productService.findOneProduct(detail.getProductId());
			inventoryCheckDetail.setDetailId(inventoryCheckDetailsService.newDetailId());
			inventoryCheckDetail.setProduct(product);
			inventoryCheckDetail.setInventoryCheck(inventoryCheck);
			System.out.println(detail.getCurrentInventory());
			inventoryCheckDetail.setCurrentInventory(detail.getCurrentInventory());
			inventoryCheckDetail.setActualInventory(detail.getActualInventory());
			inventoryCheckDetail.setDifferentialInventory(detail.getActualInventory() - detail.getCurrentInventory());
			inventoryCheckDetail.setRemark(detail.getRemark());

			inventoryCheckDetailsService.addInventoryCheckDetail(inventoryCheckDetail);

			HashMap<String, Object> response = new HashMap<>();

			response.put("existingProducts", existingProducts);
			response.put("failCount", existingProducts.size());

		}
		if (validDetails.size() == 0) {
			inventoryCheckRepo.delete(inventoryCheck);
		}
		return ResponseEntity.ok(existingProducts);

	}

	public void updateInventoryCheck(InventoryCheckBean inventoryCheckBean) {
		String inventoryCheckId = inventoryCheckBean.getInventoryCheckId();
		Optional<InventoryCheckBean> optional = inventoryCheckRepo.findById(inventoryCheckId);

		if (optional.isPresent()) {
			InventoryCheckBean inventoryCheck = optional.get();
			inventoryCheck.setVerifyStatus(true);
			inventoryCheck.setVerifyEmployee(inventoryCheck.getVerifyEmployee());
			inventoryCheckRepo.save(inventoryCheck);
			List<InventoryCheckDetailsBean> details = inventoryCheck.getDetails();
			for (InventoryCheckDetailsBean detail : details) {
				if (detail.getCurrentInventory() != detail.getActualInventory()) {
					ProductBean product = productService.findOneProduct(detail.getProduct().getProductId());
					productService.updateProductByInsertCheck(product.getProductId(), detail.getActualInventory());
				}
			}
		}

	}

	public void deleteInventoryCheck(String inventoryCheckId) {
		inventoryCheckRepo.deleteById(inventoryCheckId);
	}

	public String newCheckId() {

		String maxId = inventoryCheckRepo.findMaxId();
		if (maxId == null || maxId.isEmpty()) {
			return "IC001";
		}
		String eng = maxId.substring(0, 3);
		String numStr = maxId.substring(3);
		int num = Integer.parseInt(numStr);
		String result = eng + String.format("%02d", num + 1);
		return result;
	}

}
