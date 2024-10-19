package marketMaster.service.checkout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import marketMaster.DTO.checkout.CheckoutDTO;
import marketMaster.DTO.checkout.ReturnDetailDTO;
import marketMaster.DTO.checkout.ReturnProductDTO;
import marketMaster.bean.checkout.ReturnDetailsBean;
import marketMaster.bean.checkout.ReturnProductBean;
import marketMaster.bean.employee.EmpBean;
import marketMaster.bean.product.ProductBean;
import marketMaster.exception.DataAccessException;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
@Transactional
public class ReturnProductService {
    
    private static final Logger logger = Logger.getLogger(ReturnProductService.class.getName());
    
    @Autowired
    private ReturnProductRepository returnProductRepository;

    @Autowired
    private ReturnDetailsRepository returnDetailsRepository;
    
    // 獲取單一退貨記錄
    public ReturnProductBean getReturnProduct(String returnId) throws DataAccessException {
        return returnProductRepository.findById(returnId)
                .orElseThrow(() -> new DataAccessException("退貨記錄不存在"));
    }

    // 獲取所有退貨記錄
    public List<ReturnProductBean> getAllReturnProducts() throws DataAccessException {
        return returnProductRepository.findAll();
    }

    // 新增退貨記錄
    public void addReturnProduct(ReturnProductBean returnProduct) throws DataAccessException {
        returnProductRepository.save(returnProduct);
    }

    // 更新退貨記錄
    public void updateReturnProduct(ReturnProductBean returnProduct) throws DataAccessException {
        returnProductRepository.save(returnProduct);
    }

    // 刪除退貨記錄
    public void deleteReturnProduct(String returnId) throws DataAccessException {
        returnProductRepository.deleteById(returnId);
    }

    // 更新退貨總金額
    public void updateTotalPrice(String returnId) throws DataAccessException {
        returnProductRepository.updateTotalPrice(returnId);
    }

    // 獲取退貨總表
    public List<Map<String, Object>> getReturnSummary() throws DataAccessException {
        return returnProductRepository.getReturnSummary();
    }

    // 獲取每日退貨總金額報告
    public List<Map<String, Object>> getDailyReturnReport() throws DataAccessException {
        return returnProductRepository.getDailyReturnReport();
    }

    // 根據日期範圍獲取退貨記錄
    public List<ReturnProductBean> getReturnsByDateRange(Date startDate, Date endDate) throws DataAccessException {
        return returnProductRepository.getReturnsByDateRange(startDate, endDate);
    }

    // 生成下一個退貨ID
    public String generateNextReturnId() throws DataAccessException {
        List<String> lastIds = returnProductRepository.getLastReturnId();
        String lastId = lastIds.isEmpty() ? "T00000000" : lastIds.get(0);
        int nextNumber = Integer.parseInt(lastId.substring(1)) + 1;
        return String.format("T%08d", nextNumber);
    }

    // 根據員工ID獲取退貨記錄
    public List<ReturnProductBean> getReturnsByEmployeeId(String employeeId) throws DataAccessException {
        return returnProductRepository.findByEmployeeId(employeeId);
    }

    // 獲取所有員工
    public List<EmpBean> getAllEmployees() throws DataAccessException {
        try {
            List<EmpBean> employees = returnProductRepository.getAllEmployees();
            logger.info("從資料庫獲取到 " + employees.size() + " 名員工資料");
            return employees;
        } catch (Exception e) {
            logger.severe("獲取所有員工資料時發生異常: " + e.getMessage());
            throw new DataAccessException("獲取所有員工資料失敗", e);
        }
    }

    // 根據類別獲取產品名稱
    public List<ProductBean> getProductNamesByCategory(String category) throws DataAccessException {
        return returnProductRepository.getProductNamesByCategory(category);
    }

    // 獲取特定日期的退貨總額
    public Integer getDailyReturnTotal(Date date) throws DataAccessException {
        return returnProductRepository.getDailyReturnTotal(date);
    }

    // 獲取退貨率最高的前N個商品
    public List<Map<String, Object>> getTopReturnRateProducts(int n) throws DataAccessException {
        return returnProductRepository.getTopReturnRateProducts(PageRequest.of(0, n));
    }

    // 根據原始結帳ID獲取退貨記錄
    public List<ReturnProductBean> getReturnsByOriginalCheckoutId(String originalCheckoutId) throws DataAccessException {
        return returnProductRepository.findByOriginalCheckoutId(originalCheckoutId);
    }

    // 根據原始發票號碼獲取退貨記錄
    public ReturnProductBean findByOriginalInvoiceNumber(String originalInvoiceNumber) throws DataAccessException {
        return returnProductRepository.findByOriginalInvoiceNumber(originalInvoiceNumber);
    }

    // 獲取特定時間段內的退貨總額
    public Integer getTotalReturnAmountInDateRange(Date startDate, Date endDate) throws DataAccessException {
        return returnProductRepository.getTotalReturnAmountInDateRange(startDate, endDate);
    }

    // 獲取特定員工在特定時間段內處理的退貨總額
    public Integer getEmployeeReturnAmountInDateRange(String employeeId, Date startDate, Date endDate) throws DataAccessException {
        return returnProductRepository.getEmployeeReturnAmountInDateRange(employeeId, startDate, endDate);
    }
    
    // 獲取在職員工
    public List<EmpBean> getActiveEmployees() throws DataAccessException {
        try {
            List<EmpBean> employees = returnProductRepository.getActiveEmployees();
            logger.info("從資料庫獲取到 " + employees.size() + " 名在職員工資料");
            return employees;
        } catch (Exception e) {
            logger.severe("獲取在職員工資料時發生異常: " + e.getMessage());
            throw new DataAccessException("獲取在職員工資料失敗", e);
        }
    }

    

    // 新增退貨記錄及明細
    @Transactional(rollbackFor = Exception.class)
    public String addReturnProductWithDetails(ReturnProductDTO returnData) throws DataAccessException {
        try {
        	logger.info("開始新增退貨記錄，退貨ID: " + returnData.getReturnId());
            // 處理退貨主表資料
            ReturnProductBean returnProduct = new ReturnProductBean();
            returnProduct.setReturnId(returnData.getReturnId());
            returnProduct.setOriginalInvoiceNumber(returnData.getOriginalInvoiceNumber());
            returnProduct.setOriginalCheckoutId(returnData.getOriginalCheckoutId());
            returnProduct.setEmployeeId(returnData.getEmployeeId());
            returnProduct.setReturnTotalPrice(returnData.getReturnTotalPrice());
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date returnDate = dateFormat.parse(returnData.getReturnDate());
            returnProduct.setReturnDate(returnDate);
            
            returnProductRepository.save(returnProduct);
            logger.info("退貨主表數據保存成功，ID: " + returnProduct.getReturnId());

            // 處理退貨明細資料
            for (ReturnDetailDTO detailDTO : returnData.getReturnProducts()) {
                ReturnDetailsBean returnDetail = new ReturnDetailsBean();
                returnDetail.setReturnId(returnData.getReturnId());
                returnDetail.setOriginalCheckoutId(returnData.getOriginalCheckoutId());
                returnDetail.setProductId(detailDTO.getProductId());
                returnDetail.setReasonForReturn(detailDTO.getReasonForReturn());
                returnDetail.setNumberOfReturn(detailDTO.getNumberOfReturn());
                returnDetail.setProductPrice(detailDTO.getProductPrice());
                returnDetail.setReturnPrice(detailDTO.getReturnPrice());
                
                logger.info("正在處理退貨商品，狀態為: " + detailDTO.getReturnStatus());
                logger.info("完整的退貨商品資訊: " + detailDTO.toString());
                
                // 驗證退貨狀態
                if (!isValidReturnStatus(detailDTO.getReturnStatus())) {
                    logger.warning("檢測到無效的退貨狀態: " + detailDTO.getReturnStatus());
                    throw new IllegalArgumentException("無效的退貨狀態: " + detailDTO.getReturnStatus());
                }
                returnDetail.setReturnStatus(detailDTO.getReturnStatus());
                
                if (detailDTO.getReturnPhoto() != null && !detailDTO.getReturnPhoto().isEmpty()) {
                    returnDetail.setReturnPhoto(detailDTO.getReturnPhoto());
                }

                returnDetailsRepository.save(returnDetail);
                logger.info("退貨明細保存成功，ReturnId: " + returnDetail.getReturnId() + 
                        ", ProductId: " + returnDetail.getProductId() +
                        ", PhotoPath: " + returnDetail.getReturnPhoto());
            }
            
            logger.info("退貨記錄及明細新增完成");
            return returnData.getReturnId();
        } catch (Exception e) {
        	logger.severe("新增退貨記錄及明細時發生異常: " + e.getMessage());
            logger.severe("完整的退貨數據: " + returnData.toString());
            e.printStackTrace(); // 在控制台打印完整的堆棧跟踪
            throw new DataAccessException("新增退貨記錄及明細失敗", e);
        }
    }
    
    public List<CheckoutDTO> getInvoiceDetails(String invoiceNumber) throws DataAccessException {
        try {
            List<CheckoutDTO> products = returnProductRepository.getInvoiceProducts(invoiceNumber);
            String originalCheckoutId = products.isEmpty() ? null : products.get(0).getOriginalCheckoutId();
            for (CheckoutDTO product : products) {
                product.setOriginalCheckoutId(originalCheckoutId);
            }
            return products;
        } catch (Exception e) {
            logger.severe("獲取發票詳細資料時發生異常: " + e.getMessage());
            throw new DataAccessException("獲取發票詳細資料失敗", e);
        }
    }
    
    private boolean isValidReturnStatus(String status) {
        List<String> validStatuses = Arrays.asList("顧客因素", "商品外觀損傷", "商品品質異常");
        logger.info("驗證退貨狀態: " + status + ", 是否有效: " + validStatuses.contains(status));
        return validStatuses.contains(status);
    }
    
}