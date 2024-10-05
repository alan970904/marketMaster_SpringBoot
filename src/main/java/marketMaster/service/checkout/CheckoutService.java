package marketMaster.service.checkout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import marketMaster.bean.checkout.CheckoutBean;
import marketMaster.bean.checkout.CheckoutDetailsBean;
import marketMaster.bean.employee.EmpBean;
import marketMaster.bean.product.ProductBean;
import marketMaster.controller.checkout.CheckoutController;
import marketMaster.service.checkout.CheckoutRepository;
import marketMaster.service.checkout.CheckoutDetailsRepository;
import marketMaster.exception.DataAccessException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
@Transactional
public class CheckoutService {

	private static final Logger logger = Logger.getLogger(CheckoutService.class.getName());

	@Autowired
	private CheckoutRepository checkoutRepository;

	@Autowired
	private CheckoutDetailsRepository checkoutDetailsRepository;

	@Autowired
	private CheckoutDetailsService checkoutDetailsService;

	private ObjectMapper objectMapper = new ObjectMapper();

	public CheckoutBean getCheckout(String checkoutId) throws DataAccessException {
		return checkoutRepository.findById(checkoutId).orElseThrow(() -> new DataAccessException("結帳記錄不存在"));
	}

	public List<CheckoutBean> getAllCheckouts() throws DataAccessException {
		return checkoutRepository.findAll();
	}

	public void addCheckout(CheckoutBean checkout) throws DataAccessException {
		checkoutRepository.save(checkout);
	}

	public void deleteCheckout(String checkoutId) throws DataAccessException {
		checkoutRepository.deleteById(checkoutId);
	}

	@Transactional
	public boolean updateCheckout(CheckoutBean checkout) throws DataAccessException {
		try {
			// 獲取原有的結帳資訊，包括關聯的結帳明細
			CheckoutBean existingCheckout = checkoutRepository.findById(checkout.getCheckoutId())
					.orElseThrow(() -> new DataAccessException("結帳記錄不存在"));

			// 更新結帳基本資訊
			existingCheckout.setCustomerTel(checkout.getCustomerTel());
			existingCheckout.setEmployeeId(checkout.getEmployeeId());
			existingCheckout.setCheckoutDate(checkout.getCheckoutDate());
			existingCheckout.setPointsDueDate(checkout.getPointsDueDate());

			// 保存更新後的結帳資訊
			checkoutRepository.save(existingCheckout);

			// 更新總金額（如果需要）
			updateTotalPrice(existingCheckout.getCheckoutId());

			return true;
		} catch (Exception e) {
			e.printStackTrace(); // 在伺服器日誌中打印詳細錯誤
			throw new DataAccessException("更新結帳記錄失敗: " + e.getMessage());
		}
	}

	public List<CheckoutBean> searchCheckoutsByTel(String customerTel) throws DataAccessException {
		return checkoutRepository.searchByTel(customerTel);
	}

	public List<Map<String, Object>> getDailySalesReport() throws DataAccessException {
		return checkoutRepository.getDailySalesReport();
	}

	public List<Map<String, Object>> getCheckoutSummary() throws DataAccessException {
		return checkoutRepository.getCheckoutSummary();
	}

	public String generateNextCheckoutId() throws DataAccessException {
		List<String> lastIds = checkoutRepository.getLastCheckoutId();
		String lastId = lastIds.isEmpty() ? "C00000000" : lastIds.get(0);
		if (!lastId.matches("C\\d{8}")) {
			return "C00000001";
		}
		int nextNumber = Integer.parseInt(lastId.substring(1)) + 1;
		return String.format("C%08d", nextNumber);
	}

	public List<EmpBean> getAllEmployees() throws DataAccessException {
		try {
			List<EmpBean> employees = checkoutRepository.getAllEmployees();
			logger.info("從資料庫獲取到 " + employees.size() + " 名員工資料");
			return employees;
		} catch (Exception e) {
			logger.severe("獲取所有員工資料時發生異常: " + e.getMessage());
			throw new DataAccessException("獲取所有員工資料失敗", e);
		}
	}

	public List<ProductBean> getProductNamesByCategory(String category) throws DataAccessException {
		return checkoutRepository.getProductNamesByCategory(category);
	}

	public List<CheckoutDetailsBean> parseCheckoutDetails(String detailsJson) throws DataAccessException {
		try {
			return objectMapper.readValue(detailsJson, new TypeReference<List<CheckoutDetailsBean>>() {
			});
		} catch (IOException e) {
			throw new DataAccessException("解析結帳明細失敗", e);
		}
	}

	@Transactional
	public boolean insertCheckoutWithDetails(CheckoutBean checkout, List<CheckoutDetailsBean> details)
			throws DataAccessException {
		try {
			int totalAmount = 0;

			checkoutRepository.save(checkout); // 先保存 CheckoutBean

			for (CheckoutDetailsBean detail : details) {
				detail.setCheckoutId(checkout.getCheckoutId());
				detail.setCheckoutPrice(detail.getProductPrice() * detail.getNumberOfCheckout());
				checkoutDetailsRepository.save(detail);
				totalAmount += detail.getCheckoutPrice();
			}

			checkout.setCheckoutTotalPrice(totalAmount);
			int bonusPoints = calculateBonusPoints(totalAmount);
			checkout.setBonusPoints(bonusPoints);

			// 設置紅利點數到期日（一年後）
			LocalDate pointsDueDate = LocalDate.now().plusYears(1);
			checkout.setPointsDueDate(java.sql.Date.valueOf(pointsDueDate));

			checkoutRepository.save(checkout);// 更新 CheckoutBean

			return true;
		} catch (Exception e) {
			throw new DataAccessException("新增結帳記錄和明細失敗", e);
		}
	}

	@Transactional
	public void deleteCheckoutAndDetails(String checkoutId) throws DataAccessException {
		if (checkoutId == null || checkoutId.isEmpty()) {
			throw new DataAccessException("Invalid checkoutId");
		}
		checkoutDetailsRepository.deleteByCheckoutId(checkoutId);
		checkoutRepository.deleteById(checkoutId);
	}

	@Transactional
	public void updateTotalAndBonus(String checkoutId, BigDecimal totalAmount, int bonusPoints)
			throws DataAccessException {
		try {
			checkoutRepository.updateTotalAndBonus(checkoutId, totalAmount, bonusPoints);
		} catch (Exception e) {
			throw new DataAccessException("更新總金額和紅利點數失敗", e);
		}
	}

	private BigDecimal calculateTotalAmount(List<CheckoutDetailsBean> details) {
		return details.stream().map(detail -> new BigDecimal(detail.getCheckoutPrice())).reduce(BigDecimal.ZERO,
				BigDecimal::add);
	}

	private int calculateBonusPoints(int totalAmount) {
		return totalAmount / 100;
	}

	// 新增的方法，用於更新結帳總價
	@Transactional
	public void updateTotalPrice(String checkoutId) throws DataAccessException {
		int totalPrice = checkoutDetailsService.calculateCheckoutTotal(checkoutId);
		CheckoutBean checkout = checkoutRepository.findById(checkoutId)
				.orElseThrow(() -> new DataAccessException("結帳記錄不存在"));
		checkout.setCheckoutTotalPrice(totalPrice);
		checkout.setBonusPoints(calculateBonusPoints(totalPrice));
		checkoutRepository.save(checkout);
	}

	// 新增的方法，用於處理退貨
	@Transactional
	public void processReturn(String checkoutId, String productId, int returnQuantity, int returnPrice)
			throws DataAccessException {
		checkoutDetailsService.updateAfterReturn(checkoutId, productId, returnQuantity, returnPrice);
		updateTotalPrice(checkoutId);
	}

	// 新增的方法，用於取消退貨
	@Transactional
	public void cancelReturn(String checkoutId, String productId, int returnQuantity, int returnPrice)
			throws DataAccessException {
		checkoutDetailsService.cancelReturn(checkoutId, productId, returnQuantity, returnPrice);
		updateTotalPrice(checkoutId);
	}
}