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
import java.util.ArrayList;
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
        return checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> new DataAccessException("結帳記錄不存在"));
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
            logger.info("Updating checkout: " + checkout.getCheckoutId());
            
            // 獲取原有的結帳資訊
            CheckoutBean existingCheckout = checkoutRepository.findById(checkout.getCheckoutId())
                    .orElseThrow(() -> new DataAccessException("結帳記錄不存在"));

            // 更新結帳基本資訊
            existingCheckout.setEmployeeId(checkout.getEmployeeId());
            existingCheckout.setCheckoutDate(checkout.getCheckoutDate());
            
            // 處理客戶電話
            String customerTel = checkout.getCustomerTel();
            existingCheckout.setCustomerTel(customerTel);
            
            if (customerTel == null || customerTel.trim().isEmpty()) {
                logger.info("Customer tel is empty, setting bonus points to 0");
                existingCheckout.setBonusPoints(0);
                existingCheckout.setPointsDueDate(null);
            } else {
                logger.info("Customer tel is not empty, setting bonus points and due date");
                existingCheckout.setBonusPoints(checkout.getBonusPoints());
                existingCheckout.setPointsDueDate(checkout.getPointsDueDate());
            }

            // 保存更新後的結帳資訊
            checkoutRepository.save(existingCheckout);

            // 更新總金額
            updateTotalPrice(existingCheckout.getCheckoutId());

            logger.info("Checkout updated successfully: " + existingCheckout.getCheckoutId());
            return true;
        } catch (Exception e) {
            logger.severe("更新結帳記錄失敗: " + e.getMessage());
            e.printStackTrace();
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
            return objectMapper.readValue(detailsJson, new TypeReference<List<CheckoutDetailsBean>>() {});
        } catch (IOException e) {
            throw new DataAccessException("解析結帳明細失敗", e);
        }
    }

    @Transactional
    public boolean insertCheckoutWithDetails(CheckoutBean checkout, List<CheckoutDetailsBean> details) throws DataAccessException {
        try {
            int totalAmount = 0;
            
            checkoutRepository.save(checkout);  // 先保存 CheckoutBean

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
            if (checkoutId == null || checkoutId.trim().isEmpty()) {
                throw new IllegalArgumentException("checkoutId 不能為空");
            }
            logger.info("Updating total and bonus for checkout: " + checkoutId);
            CheckoutBean checkout = checkoutRepository.findById(checkoutId)
                    .orElseThrow(() -> new DataAccessException("結帳記錄不存在"));
            
            checkout.setCheckoutTotalPrice(totalAmount.intValue());
            
            String customerTel = checkout.getCustomerTel();
            if (customerTel == null || customerTel.trim().isEmpty()) {
                logger.info("Customer tel is empty, setting bonus points to 0");
                checkout.setBonusPoints(0);
                checkout.setPointsDueDate(null);
            } else {
                logger.info("Customer tel is not empty, setting bonus points: " + bonusPoints);
                checkout.setBonusPoints(bonusPoints);
                // 設置紅利點數到期日（一年後）
                LocalDate pointsDueDate = LocalDate.now().plusYears(1);
                checkout.setPointsDueDate(java.sql.Date.valueOf(pointsDueDate));
            }
            
            checkoutRepository.save(checkout);
            logger.info("Total and bonus updated successfully for checkout: " + checkoutId);
        } catch (Exception e) {
            logger.severe("更新總金額和紅利點數失敗: " + e.getMessage());
            throw new DataAccessException("更新總金額和紅利點數失敗", e);
        }
    }

    private BigDecimal calculateTotalAmount(List<CheckoutDetailsBean> details) {
        return details.stream()
                .map(detail -> new BigDecimal(detail.getCheckoutPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
    public void processReturn(String checkoutId, String productId, int returnQuantity, int returnPrice) throws DataAccessException {
        checkoutDetailsService.updateAfterReturn(checkoutId, productId, returnQuantity, returnPrice);
        updateTotalPrice(checkoutId);
    }

    // 新增的方法，用於取消退貨
    @Transactional
    public void cancelReturn(String checkoutId, String productId, int returnQuantity, int returnPrice) throws DataAccessException {
        checkoutDetailsService.cancelReturn(checkoutId, productId, returnQuantity, returnPrice);
        updateTotalPrice(checkoutId);
    }
    
    private List<CheckoutDetailsBean> updateCheckoutDetails(String checkoutId, Map<String, String> checkoutData) throws Exception {
        List<CheckoutDetailsBean> updatedDetails = new ArrayList<>();
        
        // 假設結帳明細數據以 JSON 字符串的形式存在於 checkoutData 中
        String detailsJson = checkoutData.get("checkoutDetails");
        if (detailsJson != null && !detailsJson.isEmpty()) {
            List<Map<String, Object>> detailsList = objectMapper.readValue(detailsJson, new TypeReference<List<Map<String, Object>>>() {});
            
            for (Map<String, Object> detail : detailsList) {
                CheckoutDetailsBean detailBean = new CheckoutDetailsBean();
                detailBean.setCheckoutId(checkoutId);
                detailBean.setProductId((String) detail.get("productId"));
                detailBean.setNumberOfCheckout((Integer) detail.get("quantity"));
                detailBean.setProductPrice((Integer) detail.get("price"));
                
                // 調用 service 方法來更新或插入明細
                checkoutDetailsService.updateOrInsertDetail(detailBean);
                updatedDetails.add(detailBean);
            }
        }
        
        return updatedDetails;
    }
    
}