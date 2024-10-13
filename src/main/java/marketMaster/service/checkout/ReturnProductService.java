package marketMaster.service.checkout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import marketMaster.bean.checkout.ReturnProductBean;
import marketMaster.bean.employee.EmpBean;
import marketMaster.bean.product.ProductBean;
import marketMaster.exception.DataAccessException;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ReturnProductService {
    @Autowired
    private ReturnProductRepository returnProductRepository;

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
        String lastId = lastIds.isEmpty() ? "R00000000" : lastIds.get(0);
        int nextNumber = Integer.parseInt(lastId.substring(1)) + 1;
        return String.format("R%08d", nextNumber);
    }

    // 根據員工ID獲取退貨記錄
    public List<ReturnProductBean> getReturnsByEmployeeId(String employeeId) throws DataAccessException {
        return returnProductRepository.findByEmployeeId(employeeId);
    }

    // 獲取所有員工
    public List<EmpBean> getAllEmployees() throws DataAccessException {
        return returnProductRepository.getAllEmployees();
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
}