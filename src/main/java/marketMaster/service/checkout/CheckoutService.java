package marketMaster.service.checkout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import marketMaster.bean.checkout.CheckoutBean;
import marketMaster.bean.checkout.CheckoutDetailsBean;
import marketMaster.bean.employee.EmpBean;
import marketMaster.bean.product.ProductBean;
import marketMaster.service.checkout.CheckoutRepository;
import marketMaster.service.checkout.CheckoutDetailsRepository;
import marketMaster.exception.DataAccessException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CheckoutService {
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

    public boolean updateCheckout(CheckoutBean checkout) throws DataAccessException {
        checkoutRepository.save(checkout);
        return true;
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
        return checkoutRepository.getAllEmployees();
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
            checkoutRepository.save(checkout);

            for (CheckoutDetailsBean detail : details) {
                detail.setCheckoutId(checkout.getCheckoutId());
                checkoutDetailsRepository.save(detail);
            }

            BigDecimal totalAmount = calculateTotalAmount(details);
            int bonusPoints = calculateBonusPoints(totalAmount);
            checkout.setCheckoutTotalPrice(totalAmount.intValue());
            checkout.setBonusPoints(bonusPoints);
            checkoutRepository.save(checkout);

            return true;
        } catch (Exception e) {
            throw new DataAccessException("新增結帳記錄和明細失敗", e);
        }
    }

    @Transactional
    public void deleteCheckoutAndDetails(String checkoutId) throws DataAccessException {
        checkoutDetailsRepository.deleteByCheckoutId(checkoutId);
        checkoutRepository.deleteById(checkoutId);
    }

    public void updateTotalAndBonus(String checkoutId, BigDecimal totalAmount, int bonusPoints) throws DataAccessException {
        checkoutRepository.updateTotalAndBonus(checkoutId, totalAmount, bonusPoints);
    }

    private BigDecimal calculateTotalAmount(List<CheckoutDetailsBean> details) {
        return details.stream()
                .map(detail -> new BigDecimal(detail.getCheckoutPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int calculateBonusPoints(BigDecimal totalAmount) {
        return totalAmount.divide(new BigDecimal(100), 0, BigDecimal.ROUND_DOWN).intValue();
    }

    // 新增的方法，用於更新結帳總價
    @Transactional
    public void updateTotalPrice(String checkoutId) throws DataAccessException {
        checkoutRepository.updateTotalPrice(checkoutId);
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
}