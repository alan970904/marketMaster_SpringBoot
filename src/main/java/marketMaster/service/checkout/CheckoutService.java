package marketMaster.service.checkout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import marketMaster.DTO.checkout.CartDTO;
import marketMaster.bean.checkout.CheckoutBean;
import marketMaster.bean.checkout.CheckoutDetailsBean;
import marketMaster.bean.employee.EmpBean;
import marketMaster.bean.product.ProductBean;
import marketMaster.controller.checkout.CheckoutController;
import marketMaster.service.checkout.CheckoutRepository;
import marketMaster.service.product.ProductService;
import marketMaster.service.checkout.CheckoutDetailsRepository;
import marketMaster.exception.DataAccessException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
    
    @Autowired
    private ProductService productService;
    
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
            
            CheckoutBean existingCheckout = checkoutRepository.findById(checkout.getCheckoutId())
                    .orElseThrow(() -> new DataAccessException("結帳記錄不存在"));

            existingCheckout.setEmployeeId(checkout.getEmployeeId());
            existingCheckout.setCheckoutDate(checkout.getCheckoutDate());
            existingCheckout.setInvoiceNumber(checkout.getInvoiceNumber());
            existingCheckout.setCheckoutStatus(checkout.getCheckoutStatus());
            
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

            checkoutRepository.save(existingCheckout);

            updateTotalPrice(existingCheckout.getCheckoutId());

            logger.info("Checkout updated successfully: " + existingCheckout.getCheckoutId());
            return true;
        } catch (Exception e) {
            logger.severe("更新結帳記錄失敗: " + e.getMessage());
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
        int nextNumber = Integer.parseInt(lastId.substring(1)) + 1;
        return String.format("C%08d", nextNumber);
    }
    
    public String generateNextInvoiceNumber() throws DataAccessException {
        List<String> lastNumbers = checkoutRepository.getLastInvoiceNumber();
        String lastNumber = lastNumbers.isEmpty() ? "IN00000000" : lastNumbers.get(0);
        int nextNumber = Integer.parseInt(lastNumber.substring(2)) + 1;
        return String.format("IN%08d", nextNumber);
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
            
            checkout.setCheckoutStatus("正常");
            checkoutRepository.save(checkout);

            for (CheckoutDetailsBean detail : details) {
                detail.setCheckoutId(checkout.getCheckoutId());
                detail.setCheckoutPrice(detail.getProductPrice() * detail.getNumberOfCheckout());
                checkoutDetailsRepository.save(detail);
                totalAmount += detail.getCheckoutPrice();
            }

            checkout.setCheckoutTotalPrice(totalAmount);

            if (checkout.getCustomerTel() != null && !checkout.getCustomerTel().isEmpty()) {
                int bonusPoints = calculateBonusPoints(totalAmount);
                checkout.setBonusPoints(bonusPoints);
                checkoutRepository.addBonusPointsToCustomer(checkout.getCustomerTel(), bonusPoints);

                LocalDate pointsDueDate = LocalDate.now().plusYears(1);
                checkout.setPointsDueDate(java.sql.Date.valueOf(pointsDueDate));
            } else {
                checkout.setBonusPoints(0);
                checkout.setPointsDueDate(null);
            }

            checkoutRepository.save(checkout);

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
    public void updateTotalAndBonus(String checkoutId, int totalAmount, Integer bonusPoints) throws DataAccessException {
        try {
            if (checkoutId == null || checkoutId.trim().isEmpty()) {
                throw new IllegalArgumentException("checkoutId 不能為空");
            }
            logger.info("Updating total and bonus for checkout: " + checkoutId);
            CheckoutBean checkout = checkoutRepository.findById(checkoutId)
                    .orElseThrow(() -> new DataAccessException("結帳記錄不存在"));
            
            checkout.setCheckoutTotalPrice(totalAmount);
            
            String customerTel = checkout.getCustomerTel();
            if (customerTel == null || customerTel.trim().isEmpty()) {
                logger.info("Customer tel is empty, setting bonus points to 0");
                checkout.setBonusPoints(0);
                checkout.setPointsDueDate(null);
            } else {
                logger.info("Customer tel is not empty, setting bonus points: " + bonusPoints);
                checkout.setBonusPoints(bonusPoints);
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
        
        String detailsJson = checkoutData.get("checkoutDetails");
        if (detailsJson != null && !detailsJson.isEmpty()) {
            List<Map<String, Object>> detailsList = objectMapper.readValue(detailsJson, new TypeReference<List<Map<String, Object>>>() {});
            
            for (Map<String, Object> detail : detailsList) {
                CheckoutDetailsBean detailBean = new CheckoutDetailsBean();
                detailBean.setCheckoutId(checkoutId);
                detailBean.setProductId((String) detail.get("productId"));
                detailBean.setNumberOfCheckout((Integer) detail.get("quantity"));
                detailBean.setProductPrice((Integer) detail.get("price"));
                
                checkoutDetailsService.updateOrInsertDetail(detailBean);
                updatedDetails.add(detailBean);
            }
        }
        
        return updatedDetails;
    }
    
 // 新增方法：根據日期範圍獲取結帳記錄
    public List<CheckoutBean> getCheckoutsByDateRange(Date startDate, Date endDate) throws DataAccessException {
        return checkoutRepository.getCheckoutsByDateRange(startDate, endDate);
    }

    // 新增方法：獲取特定日期的銷售總額
    public Integer getDailySalesTotal(Date date) throws DataAccessException {
        return checkoutRepository.getDailySalesTotal(date);
    }
    
    @Transactional
    public void updateCheckoutStatus(String checkoutId, String status) throws DataAccessException {
        CheckoutBean checkout = checkoutRepository.findById(checkoutId)
            .orElseThrow(() -> new DataAccessException("結帳記錄不存在"));
            
        if (status.equals("已退貨") && checkout.getCheckoutStatus().equals("正常")) {
            // 退貨時扣除紅利
            checkoutRepository.deductBonusPointsFromCustomer(
                checkout.getCustomerTel(), 
                checkout.getBonusPoints()
            );
        }
        
        checkoutRepository.updateCheckoutStatus(checkoutId, status);
    }

    public CheckoutBean findByInvoiceNumber(String invoiceNumber) throws DataAccessException {
        return checkoutRepository.findByInvoiceNumber(invoiceNumber);
    }

    @Transactional
    public void updateTotalPriceAfterReturn(String checkoutId) throws DataAccessException {
        checkoutRepository.updateTotalPriceAfterReturn(checkoutId);
    }
    
    /**
     * 處理購物車結帳
     */
    @Transactional
    public boolean processCartCheckout(CheckoutBean checkout, List<CheckoutDetailsBean> details) throws DataAccessException {
        try {
            // 檢查所有商品庫存
            for (CheckoutDetailsBean detail : details) {
                if (!productService.checkAndUpdateStock(detail.getProductId(), detail.getNumberOfCheckout())) {
                    // 如果有任何商品庫存不足，回滾之前的庫存更新
                    for (CheckoutDetailsBean rollbackDetail : details) {
                        if (rollbackDetail == detail) break;
                        productService.cancelStockUpdate(
                            rollbackDetail.getProductId(), 
                            rollbackDetail.getNumberOfCheckout()
                        );
                    }
                    throw new DataAccessException("商品庫存不足");
                }
            }

            // 進行結帳處理
            return insertCheckoutWithDetails(checkout, details);
        } catch (Exception e) {
            // 發生異常時回滾所有庫存更新
            for (CheckoutDetailsBean detail : details) {
                productService.cancelStockUpdate(
                    detail.getProductId(), 
                    detail.getNumberOfCheckout()
                );
            }
            throw new DataAccessException("結帳處理失敗: " + e.getMessage());
        }
    }

    /**
     * 驗證購物車內容
     * @return Map 包含驗證結果和錯誤信息
     */
    public Map<String, Object> validateCartItems(List<CheckoutDetailsBean> details) {
        Map<String, Object> result = new HashMap<>();
        Map<String, String> invalidItems = new HashMap<>();
        boolean isValid = true;

        for (CheckoutDetailsBean detail : details) {
            ProductBean product = productService.getProduct(detail.getProductId());
            if (product == null) {
                isValid = false;
                invalidItems.put(detail.getProductId(), "商品不存在");
            } else if (product.getNumberOfInventory() < detail.getNumberOfCheckout()) {
                isValid = false;
                invalidItems.put(detail.getProductId(), 
                    "庫存不足，目前剩餘: " + product.getNumberOfInventory());
            }
        }

        result.put("isValid", isValid);
        if (!isValid) {
            result.put("invalidItems", invalidItems);
        }
        return result;
    }
    
 // 在 CheckoutService.java 中新增以下方法
    @Transactional
    public boolean processCartCheckout(CartDTO cartDTO, String customerTel, String employeeId) throws DataAccessException {
        try {
            // 創建結帳記錄
            CheckoutBean checkout = new CheckoutBean();
            checkout.setCheckoutId(generateNextCheckoutId());
            checkout.setCustomerTel(customerTel);
            checkout.setEmployeeId(employeeId);
            checkout.setCheckoutDate(new Date());
            checkout.setInvoiceNumber(generateNextInvoiceNumber());
            checkout.setCheckoutStatus("正常");

            // 轉換購物車項目為結帳明細
            List<CheckoutDetailsBean> details = cartDTO.getItems().stream()
                .map(item -> {
                    CheckoutDetailsBean detail = new CheckoutDetailsBean();
                    detail.setCheckoutId(checkout.getCheckoutId());
                    detail.setProductId(item.getProductId());
                    detail.setNumberOfCheckout(item.getQuantity());
                    detail.setProductPrice(item.getPrice());
                    return detail;
                })
                .collect(Collectors.toList());

            // 執行結帳處理
            return insertCheckoutWithDetails(checkout, details);
        } catch (Exception e) {
            throw new DataAccessException("處理購物車結帳失敗: " + e.getMessage());
        }
    }
    
}