package marketMaster.service.checkout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import marketMaster.bean.checkout.ReturnDetailsBean;
import marketMaster.exception.DataAccessException;
import marketMaster.bean.checkout.ReturnDetailsBean.ReturnDetailsId;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ReturnDetailsService {
    @Autowired
    private ReturnDetailsRepository returnDetailsRepository;

    // 獲取單一退貨明細
    public ReturnDetailsBean getReturnDetails(String returnId, String originalCheckoutId, String productId) throws DataAccessException {
        return returnDetailsRepository.findById(new ReturnDetailsBean.ReturnDetailsId(returnId, originalCheckoutId, productId))
                .orElseThrow(() -> new DataAccessException("退貨明細不存在"));
    }

    // 獲取特定退貨ID的所有明細
    public List<ReturnDetailsBean> getPartReturnDetails(String returnId) throws DataAccessException {
        return returnDetailsRepository.findByReturnId(returnId);
    }

    // 獲取所有退貨明細
    public List<ReturnDetailsBean> getAllReturnDetails() throws DataAccessException {
        return returnDetailsRepository.findAll();
    }

    // 新增或更新退貨明細
    public void saveReturnDetails(ReturnDetailsBean returnDetails) throws DataAccessException {
        returnDetailsRepository.save(returnDetails);
    }

    // 刪除退貨明細
    public void deleteReturnDetails(String returnId, String originalCheckoutId, String productId) throws DataAccessException {
        returnDetailsRepository.deleteById(new ReturnDetailsBean.ReturnDetailsId(returnId, originalCheckoutId, productId));
    }

    // 根據產品ID搜索退貨明細
    public List<ReturnDetailsBean> searchReturnDetailsByProductId(String productId) throws DataAccessException {
        return returnDetailsRepository.searchByProductId(productId);
    }

    // 獲取退貨比較表
    public List<Map<String, Object>> getReturnComparisonTable() throws DataAccessException {
        return returnDetailsRepository.getReturnComparisonTable();
    }

    // 獲取產品退貨率
    public List<Map<String, Object>> getProductReturnRates() throws DataAccessException {
        return returnDetailsRepository.getProductReturnRates();
    }

    // 更新退貨狀態
    public void updateReturnStatus(String returnId, String originalCheckoutId, String productId, String status) throws DataAccessException {
        returnDetailsRepository.updateReturnStatus(returnId, originalCheckoutId, productId, status);
    }

    // 計算退貨總金額
    public int calculateReturnTotal(String returnId) throws DataAccessException {
        return returnDetailsRepository.calculateReturnTotal(returnId);
    }

    // 刪除特定退貨ID的所有明細
    public void deleteReturnDetailsById(String returnId) throws DataAccessException {
        returnDetailsRepository.deleteByReturnId(returnId);
    }

    // 獲取特定結帳ID和商品ID的退貨明細
    public List<ReturnDetailsBean> findByOriginalCheckoutIdAndProductId(String originalCheckoutId, String productId) throws DataAccessException {
        return returnDetailsRepository.findByOriginalCheckoutIdAndProductId(originalCheckoutId, productId);
    }

    // 獲取特定商品的總退貨量
    public Integer getTotalReturnsByProduct(String productId) throws DataAccessException {
        return returnDetailsRepository.getTotalReturnsByProduct(productId);
    }

    // 獲取退貨原因統計
    public List<Map<String, Object>> getReturnReasonStatistics() throws DataAccessException {
        return returnDetailsRepository.getReturnReasonStatistics();
    }

    // 根據原始結帳ID獲取退貨明細
    public List<ReturnDetailsBean> findByOriginalCheckoutId(String originalCheckoutId) throws DataAccessException {
        return returnDetailsRepository.findByOriginalCheckoutId(originalCheckoutId);
    }

    // 獲取特定時間段內的退貨明細
    public List<ReturnDetailsBean> findReturnDetailsByDateRange(Date startDate, Date endDate) throws DataAccessException {
        return returnDetailsRepository.findReturnDetailsByDateRange(startDate, endDate);
    }

    // 獲取特定商品在特定時間段內的退貨數量
    public Integer getProductReturnQuantityInDateRange(String productId, Date startDate, Date endDate) throws DataAccessException {
        return returnDetailsRepository.getProductReturnQuantityInDateRange(productId, startDate, endDate);
    }
}