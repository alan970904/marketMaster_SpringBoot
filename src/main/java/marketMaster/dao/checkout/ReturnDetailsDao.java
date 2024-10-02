package marketMaster.dao.checkout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import marketMaster.bean.checkout.ReturnProductBean;
import marketMaster.bean.checkout.ReturnDetailsBean;
import marketMaster.exception.DataAccessException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;

@Repository
public class ReturnDetailsDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    // 獲取單一退貨明細
    public ReturnDetailsBean getOne(String returnId, String checkoutId, String productId) throws DataAccessException {
        try {
            String hql = "FROM ReturnDetailsBean WHERE returnId = :returnId AND checkoutId = :checkoutId AND productId = :productId";
            Query<ReturnDetailsBean> query = getSession().createQuery(hql, ReturnDetailsBean.class);
            query.setParameter("returnId", returnId);
            query.setParameter("checkoutId", checkoutId);
            query.setParameter("productId", productId);
            return query.uniqueResult();
        } catch (Exception e) {
            throw new DataAccessException("獲取單一退貨明細失敗", e);
        }
    }

    // 獲取特定退貨ID的所有明細
    public List<ReturnDetailsBean> getPart(String returnId) throws DataAccessException {
        try {
            Query<ReturnDetailsBean> query = getSession().createQuery("FROM ReturnDetailsBean rd WHERE rd.returnId = :returnId", ReturnDetailsBean.class);
            query.setParameter("returnId", returnId);
            return query.list();
        } catch (Exception e) {
            throw new DataAccessException("獲取特定退貨ID的所有明細失敗", e);
        }
    }
    
    // 獲取所有退貨明細
    public List<ReturnDetailsBean> getAll() throws DataAccessException {
        try {
            return getSession().createQuery("FROM ReturnDetailsBean", ReturnDetailsBean.class).list();
        } catch (Exception e) {
            throw new DataAccessException("獲取所有退貨明細失敗", e);
        }
    }

    // 插入新的退貨明細
    public void insert(ReturnDetailsBean detail) throws DataAccessException {
        try {
            getSession().save(detail);
        } catch (Exception e) {
            throw new DataAccessException("插入退貨明細失敗", e);
        }
    }

    // 刪除退貨明細
    public void delete(String returnId, String checkoutId, String productId) throws DataAccessException {
        try {
            ReturnDetailsBean rd = getOne(returnId, checkoutId, productId);
            if (rd != null) {
                getSession().delete(rd);
                updateReturnTotal(returnId);
            }
        } catch (Exception e) {
            throw new DataAccessException("刪除退貨明細失敗", e);
        }
    }


    // 更新退貨明細
    public void update(ReturnDetailsBean rd) throws DataAccessException {
        try {
            getSession().update(rd);
            updateReturnTotal(rd.getReturnId());
        } catch (Exception e) {
            throw new DataAccessException("更新退貨明細失敗", e);
        }
    }

    // 搜尋特定產品ID的退貨明細
    public List<ReturnDetailsBean> searchByProductId(String productId) throws DataAccessException {
        try {
            Query<ReturnDetailsBean> query = getSession().createQuery("FROM ReturnDetailsBean rd WHERE rd.productId LIKE :productId", ReturnDetailsBean.class);
            query.setParameter("productId", "%" + productId + "%");
            return query.list();
        } catch (Exception e) {
            throw new DataAccessException("搜尋特定產品ID的退貨明細失敗", e);
        }
    }
    
 
    // 更新退貨總金額
    private void updateReturnTotal(String returnId) throws DataAccessException {
        try {
            int totalAmount = calculateReturnTotal(returnId);
            
            ReturnProductBean returnProduct = getSession().get(ReturnProductBean.class, returnId);
            if (returnProduct != null) {
                returnProduct.setReturnTotalPrice(totalAmount);
                getSession().update(returnProduct);
            }
        } catch (Exception e) {
            throw new DataAccessException("更新退貨總金額失敗", e);
        }
    }
    
    // 計算退貨總金額
    private int calculateReturnTotal(String returnId) throws DataAccessException {
        try {
            Query<Integer> query = getSession().createQuery(
                "SELECT COALESCE(CAST(SUM(rd.returnPrice) AS int), 0) " +
                "FROM ReturnDetailsBean rd WHERE rd.returnId = :returnId", 
                Integer.class
            );
            query.setParameter("returnId", returnId);
            return query.uniqueResult();
        } catch (Exception e) {
            throw new DataAccessException("計算退貨總金額失敗", e);
        }
    }


    // 獲取退貨與原始購買比較報告
    public List<Map<String, Object>> getReturnComparisonReport() throws DataAccessException {
        try {
            String hql = "SELECT rd.returnId, rd.checkoutId, rd.productId, rd.numberOfReturn, rd.returnPrice, " +
                         "cd.numberOfCheckout, cd.checkoutPrice, rd.reasonForReturn " +
                         "FROM ReturnDetailsBean rd JOIN CheckoutDetailsBean cd " +
                         "ON rd.checkoutId = cd.checkoutId AND rd.productId = cd.productId";
            Query<Object[]> query = getSession().createQuery(hql, Object[].class);
            List<Object[]> results = query.list();
            List<Map<String, Object>> report = new ArrayList<>();
            for (Object[] row : results) {
                Map<String, Object> map = new HashMap<>();
                map.put("return_id", row[0]);
                map.put("checkout_id", row[1]);
                map.put("product_id", row[2]);
                map.put("number_of_return", row[3]);
                map.put("return_price", row[4]);
                map.put("number_of_checkout", row[5]);
                map.put("checkout_price", row[6]);
                map.put("reason_for_return", row[7]);
                report.add(map);
            }
            return report;
        } catch (Exception e) {
            throw new DataAccessException("獲取退貨比較報告失敗", e);
        }
    }

    // 獲取特定時間範圍內的退貨統計
    public List<Map<String, Object>> getReturnStatistics(Date startDate, Date endDate) throws DataAccessException {
        try {
            String hql = "SELECT rd.productId, SUM(rd.numberOfReturn) as totalReturns, " +
                         "SUM(rd.returnPrice) as totalReturnAmount " +
                         "FROM ReturnDetailsBean rd " +
                         "JOIN ReturnProductBean rp ON rd.returnId = rp.returnId " +
                         "WHERE rp.returnDate BETWEEN :startDate AND :endDate " +
                         "GROUP BY rd.productId";
            Query<Object[]> query = getSession().createQuery(hql, Object[].class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            List<Object[]> results = query.list();
            List<Map<String, Object>> statistics = new ArrayList<>();
            for (Object[] row : results) {
                Map<String, Object> map = new HashMap<>();
                map.put("product_id", row[0]);
                map.put("total_returns", row[1]);
                map.put("total_return_amount", row[2]);
                statistics.add(map);
            }
            return statistics;
        } catch (Exception e) {
            throw new DataAccessException("獲取退貨統計失敗", e);
        }
    }
    
    

    // 獲取最常見的退貨原因
    public List<Map<String, Object>> getMostCommonReturnReasons(int limit) throws DataAccessException {
        try {
            String hql = "SELECT rd.reasonForReturn, COUNT(rd) as count " +
                         "FROM ReturnDetailsBean rd " +
                         "GROUP BY rd.reasonForReturn " +
                         "ORDER BY count DESC";
            Query<Object[]> query = getSession().createQuery(hql, Object[].class);
            query.setMaxResults(limit);
            List<Object[]> results = query.list();
            List<Map<String, Object>> reasons = new ArrayList<>();
            for (Object[] row : results) {
                Map<String, Object> map = new HashMap<>();
                map.put("reason", row[0]);
                map.put("count", row[1]);
                reasons.add(map);
            }
            return reasons;
        } catch (Exception e) {
            throw new DataAccessException("獲取最常見退貨原因失敗", e);
        }
    }

    // 更新退貨後的庫存
    public void updateInventoryAfterReturn(String productId, int returnQuantity) throws DataAccessException {
        try {
            String hql = "UPDATE ProductBean p SET p.productQuantity = p.productQuantity + :returnQuantity " +
                         "WHERE p.productId = :productId";
            Query<?> query = getSession().createQuery(hql);
            query.setParameter("returnQuantity", returnQuantity);
            query.setParameter("productId", productId);
            int result = query.executeUpdate();
            if (result == 0) {
                throw new DataAccessException("更新商品 " + productId + " 的庫存失敗");
            }
        } catch (Exception e) {
            throw new DataAccessException("更新庫存失敗", e);
        }
    }
}