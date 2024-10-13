package marketMaster.service.checkout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import marketMaster.bean.checkout.ReturnDetailsBean;
import marketMaster.exception.DataAccessException;
import marketMaster.bean.checkout.ReturnDetailsBean.ReturnDetailsId;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ReturnDetailsService {
    @Autowired
    private ReturnDetailsRepository returnDetailsRepository;

    // 獲取單一退貨明細
    public ReturnDetailsBean getReturnDetails(String returnId, String checkoutId, String productId) throws DataAccessException {
        return returnDetailsRepository.findById(new ReturnDetailsId(returnId, checkoutId, productId))
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

    // 新增退貨明細
    public void addReturnDetails(ReturnDetailsBean returnDetails) throws DataAccessException {
        returnDetailsRepository.save(returnDetails);
    }

    // 更新退貨明細
    public void updateReturnDetails(ReturnDetailsBean returnDetails) throws DataAccessException {
        returnDetailsRepository.save(returnDetails);
    }

    // 刪除退貨明細
    public void deleteReturnDetails(String returnId, String checkoutId, String productId) throws DataAccessException {
        returnDetailsRepository.deleteById(new ReturnDetailsId(returnId, checkoutId, productId));
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
    public void updateReturnStatus(String returnId, String checkoutId, String productId, String status) throws DataAccessException {
        returnDetailsRepository.updateReturnStatus(returnId, checkoutId, productId, status);
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
    public List<ReturnDetailsBean> findByCheckoutIdAndProductId(String checkoutId, String productId) throws DataAccessException {
        return returnDetailsRepository.findByCheckoutIdAndProductId(checkoutId, productId);
    }

    // 獲取特定商品的總退貨量
    public Integer getTotalReturnsByProduct(String productId) throws DataAccessException {
        return returnDetailsRepository.getTotalReturnsByProduct(productId);
    }

    // 獲取退貨原因統計
    public List<Map<String, Object>> getReturnReasonStatistics() throws DataAccessException {
        return returnDetailsRepository.getReturnReasonStatistics();
    }
}