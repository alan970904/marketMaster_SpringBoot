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
import java.util.stream.Collectors;

@Repository
public class ReturnProductDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    // 獲取單個退貨記錄
    public ReturnProductBean getOne(String returnId) throws DataAccessException {
        try {
            return getSession().get(ReturnProductBean.class, returnId);
        } catch (Exception e) {
            throw new DataAccessException("獲取退貨記錄失敗", e);
        }
    }

    // 獲取所有退貨記錄
    public List<ReturnProductBean> getAll() throws DataAccessException {
        try {
            return getSession().createQuery("from ReturnProductBean", ReturnProductBean.class).list();
        } catch (Exception e) {
            throw new DataAccessException("獲取所有退貨記錄失敗", e);
        }
    }

    // 插入新的退貨記錄
    public void insert(ReturnProductBean returnProduct) throws DataAccessException {
        try {
            getSession().save(returnProduct);
        } catch (Exception e) {
            throw new DataAccessException("插入退貨記錄失敗", e);
        }
    }

    // 刪除退貨記錄
    public void delete(String returnId) throws DataAccessException {
        try {
            ReturnProductBean returnProduct = getSession().get(ReturnProductBean.class, returnId);
            if (returnProduct != null) {
                getSession().delete(returnProduct);
            }
        } catch (Exception e) {
            throw new DataAccessException("刪除退貨記錄失敗", e);
        }
    }

    // 更新退貨記錄
    public boolean update(ReturnProductBean returnProduct) throws DataAccessException {
        try {
            getSession().update(returnProduct);
            return true;
        } catch (Exception e) {
            throw new DataAccessException("更新退貨記錄失敗", e);
        }
    }

    // 根據員工ID搜索退貨記錄
    public List<ReturnProductBean> searchByEmployeeId(String employeeId) throws DataAccessException {
        try {
            Query<ReturnProductBean> query = getSession().createQuery("from ReturnProductBean where employeeId like :employeeId", ReturnProductBean.class);
            query.setParameter("employeeId", "%" + employeeId + "%");
            return query.list();
        } catch (Exception e) {
            throw new DataAccessException("搜索退貨記錄失敗", e);
        }
    }

    // 更新總價
    public void updateTotalPrice(String returnId) throws DataAccessException {
        try {
            ReturnProductBean returnProduct = getSession().get(ReturnProductBean.class, returnId);
            if (returnProduct != null) {
                int totalAmount = calculateReturnTotal(returnId);
                returnProduct.setReturnTotalPrice(totalAmount);
                getSession().update(returnProduct);
            }
        } catch (Exception e) {
            throw new DataAccessException("更新退貨總金額失敗", e);
        }
    }

    // 獲取每日退貨報告
    public List<Map<String, Object>> getDailyReturnsReport() throws DataAccessException {
        try {
            String hql = "select r.returnDate as returnDate, sum(r.returnTotalPrice) as totalReturns " +
                         "from ReturnProductBean r group by r.returnDate";
            return getSession().createQuery(hql, Object[].class)
                          .getResultList()
                          .stream()
                          .map(array -> {
                              Map<String, Object> map = new HashMap<>();
                              map.put("returnDate", array[0]);
                              map.put("totalReturns", array[1]);
                              return map;
                          })
                          .collect(Collectors.toList());
        } catch (Exception e) {
            throw new DataAccessException("獲取每日退貨報告失敗", e);
        }
    }

    // 獲取退貨總表
    public List<Map<String, Object>> getReturnSummary() throws DataAccessException {
        try {
            String hql = "select r.returnDate, r.employeeId, r.returnId, rd.productId, rd.numberOfReturn, " +
                         "rd.returnPrice, r.returnTotalPrice " +
                         "from ReturnProductBean r join r.returnDetails rd";
            
            return getSession().createQuery(hql, Object[].class)
                          .getResultList()
                          .stream()
                          .map(array -> {
                              Map<String, Object> map = new HashMap<>();
                              map.put("returnDate", array[0]);
                              map.put("employeeId", array[1]);
                              map.put("returnId", array[2]);
                              map.put("productId", array[3]);
                              map.put("numberOfReturn", array[4]);
                              map.put("returnPrice", array[5]);
                              map.put("returnTotalPrice", array[6]);
                              return map;
                          })
                          .collect(Collectors.toList());
        } catch (Exception e) {
            throw new DataAccessException("獲取退貨總表失敗", e);
        }
    }

    // 獲取最新的退貨ID
    public String getLastReturnId() throws DataAccessException {
        try {
            String hql = "select r.returnId from ReturnProductBean r order by r.returnId desc";
            List<String> results = getSession().createQuery(hql, String.class).setMaxResults(1).list();
            return results.isEmpty() ? "R00000000" : results.get(0);
        } catch (Exception e) {
            throw new DataAccessException("獲取最新退貨ID失敗", e);
        }
    }
    
    // 插入退貨記錄和明細
    public boolean insertReturnWithDetails(ReturnProductBean returnProduct, List<ReturnDetailsBean> details) throws DataAccessException {
        try {
            getSession().save(returnProduct);
            for (ReturnDetailsBean detail : details) {
                detail.setReturnId(returnProduct.getReturnId());
                getSession().save(detail);
            }
            return true;
        } catch (Exception e) {
            throw new DataAccessException("插入退貨記錄和明細失敗", e);
        }
    }

    // 刪除退貨記錄和相關的退貨明細
    public void deleteReturnAndDetails(String returnId) throws DataAccessException {
        try {
            ReturnProductBean returnProduct = getSession().get(ReturnProductBean.class, returnId);
            if (returnProduct != null) {
                getSession().delete(returnProduct); // 這將級聯刪除相關的退貨明細
            }
        } catch (Exception e) {
            throw new DataAccessException("刪除退貨記錄及其相關明細失敗", e);
        }
    }


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
}