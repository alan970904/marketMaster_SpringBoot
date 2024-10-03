package marketMaster.service.checkout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import marketMaster.bean.checkout.CheckoutDetailsBean;
import marketMaster.dao.checkout.CheckoutDetailsDao;
import marketMaster.exception.DataAccessException;
import marketMaster.bean.checkout.CheckoutDetailsBean.CheckoutDetailsId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class CheckoutDetailsService {
    @Autowired
    private CheckoutDetailsDao checkoutDetailsDao;

    // 獲取單一結帳明細
    public CheckoutDetailsBean getCheckoutDetails(String checkoutId, String productId) throws DataAccessException {
        return checkoutDetailsDao.findById(new CheckoutDetailsId(checkoutId, productId))
                .orElseThrow(() -> new DataAccessException("結帳明細不存在"));
    }

    // 獲取特定結帳ID的所有明細
    public List<CheckoutDetailsBean> getPartCheckoutDetails(String checkoutId) throws DataAccessException {
        return checkoutDetailsDao.findByCheckoutId(checkoutId);
    }

    // 獲取所有結帳明細
    public List<CheckoutDetailsBean> getAllCheckoutDetails() throws DataAccessException {
        return checkoutDetailsDao.findAll();
    }

    // 新增結帳明細
    public void addCheckoutDetails(CheckoutDetailsBean checkoutDetails) throws DataAccessException {
        checkoutDetailsDao.save(checkoutDetails);
    }

    // 更新結帳明細
    public void updateCheckoutDetails(CheckoutDetailsBean checkoutDetails) throws DataAccessException {
        checkoutDetailsDao.save(checkoutDetails);
    }

    // 刪除結帳明細
    public void deleteCheckoutDetails(String checkoutId, String productId) throws DataAccessException {
        checkoutDetailsDao.deleteById(new CheckoutDetailsId(checkoutId, productId));
    }

    // 根據產品ID搜索結帳明細
    public List<CheckoutDetailsBean> searchCheckoutDetailsByProductId(String productId) throws DataAccessException {
        return checkoutDetailsDao.searchByProductId(productId);
    }

    // 獲取產品退貨率
    public List<Map<String, Object>> getProductReturnRates() throws DataAccessException {
        return checkoutDetailsDao.getProductReturnRates();
    }

    // 計算結帳總金額
    public int calculateCheckoutTotal(String checkoutId) throws DataAccessException {
        return checkoutDetailsDao.calculateCheckoutTotal(checkoutId);
    }

    // 刪除特定結帳ID的所有明細
    public void deleteCheckoutDetailsById(String checkoutId) throws DataAccessException {
        checkoutDetailsDao.deleteByCheckoutId(checkoutId);
    }

    // 更新退貨後的結帳明細
    @Transactional
    public void updateAfterReturn(String checkoutId, String productId, int returnQuantity, int returnPrice) throws DataAccessException {
        checkoutDetailsDao.updateAfterReturn(checkoutId, productId, returnQuantity, returnPrice);
    }

    // 取消退貨並更新結帳明細
    @Transactional
    public void cancelReturn(String checkoutId, String productId, int returnQuantity, int returnPrice) throws DataAccessException {
        checkoutDetailsDao.cancelReturn(checkoutId, productId, returnQuantity, returnPrice);
    }
}