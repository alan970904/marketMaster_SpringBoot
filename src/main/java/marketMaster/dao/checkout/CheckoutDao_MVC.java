package marketMaster.dao.checkout;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import marketMaster.bean.checkout.CheckoutBean;
import marketMaster.bean.checkout.CheckoutDetailsBean;
import marketMaster.bean.employee.EmpBean;
import marketMaster.bean.product.ProductBean;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.util.logging.Level;

@Repository
public class CheckoutDao_MVC {
    private static final Logger logger = Logger.getLogger(CheckoutDao_MVC.class.getName());

    @Autowired
    private SessionFactory sessionFactory;

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional(readOnly = true)
    public CheckoutBean getOne(String checkoutId) {
        try {
            return getCurrentSession().get(CheckoutBean.class, checkoutId);
        } catch (Exception e) {
            logger.severe("獲取結帳記錄失敗: " + e.getMessage());
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<CheckoutBean> getAll() {
        try {
            return getCurrentSession().createQuery("from CheckoutBean", CheckoutBean.class).list();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "獲取所有結帳記錄失敗", e);
            return new ArrayList<>();
        }
    }

    @Transactional
    public void insert(CheckoutBean checkout) {
        try {
            getCurrentSession().save(checkout);
            logger.info("結帳記錄插入成功: " + checkout.getCheckoutId());
        } catch (Exception e) {
            logger.severe("插入結帳記錄失敗: " + e.getMessage());
            throw new RuntimeException("插入結帳記錄失敗", e);
        }
    }

    @Transactional
    public void delete(String checkoutId) {
        try {
            CheckoutBean checkout = getCurrentSession().get(CheckoutBean.class, checkoutId);
            if (checkout != null) {
                getCurrentSession().delete(checkout);
            }
        } catch (Exception e) {
            logger.severe("刪除結帳記錄失敗: " + e.getMessage());
            throw new RuntimeException("刪除結帳記錄失敗", e);
        }
    }

    @Transactional
    public boolean update(CheckoutBean checkout) {
        try {
            getCurrentSession().update(checkout);
            return true;
        } catch (Exception e) {
            logger.severe("更新結帳記錄失敗: " + e.getMessage());
            return false;
        }
    }

    @Transactional(readOnly = true)
    public List<CheckoutBean> searchByTel(String customerTel) {
        try {
            Query<CheckoutBean> query = getCurrentSession().createQuery("from CheckoutBean where customerTel like :tel", CheckoutBean.class);
            query.setParameter("tel", "%" + customerTel + "%");
            return query.list();
        } catch (Exception e) {
            logger.severe("搜索結帳記錄失敗: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional
    public void updateTotalPrice(String checkoutId) {
        logger.info("開始更新結帳ID為 " + checkoutId + " 的總價");
        try {
            String hql = "SELECT SUM(cd.checkoutPrice) FROM CheckoutDetailsBean cd WHERE cd.id.checkoutId = :checkoutId";
            Query<Long> query = getCurrentSession().createQuery(hql, Long.class);
            query.setParameter("checkoutId", checkoutId);
            Long totalPrice = query.uniqueResult();

            CheckoutBean checkout = getCurrentSession().get(CheckoutBean.class, checkoutId);
            if (checkout != null && totalPrice != null) {
                checkout.setCheckoutTotalPrice(totalPrice.intValue());
                getCurrentSession().update(checkout);
                logger.info("結帳總價更新成功");
            } else {
                logger.warning("未找到結帳記錄或計算總價為空");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "更新結帳總價時發生錯誤", e);
            throw new RuntimeException("更新結帳總價失敗", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDailySalesReport() {
        try {
            String hql = "select c.checkoutDate as checkoutDate, sum(c.checkoutTotalPrice) as totalSales " +
                         "from CheckoutBean c group by c.checkoutDate";
            return getCurrentSession().createQuery(hql, Object[].class)
                          .getResultList()
                          .stream()
                          .map(array -> {
                              Map<String, Object> map = new HashMap<>();
                              map.put("checkoutDate", array[0]);
                              map.put("totalSales", array[1]);
                              return map;
                          })
                          .collect(Collectors.toList());
        } catch (Exception e) {
            logger.severe("獲取每日銷售報告失敗: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCheckoutSummary() {
        try {
            String hql = "select c.checkoutDate, c.employeeId, c.checkoutId, cd.productId, cd.numberOfCheckout, " +
                         "cd.checkoutPrice, c.checkoutTotalPrice " +
                         "from CheckoutBean c join c.checkoutDetails cd";
            
            return getCurrentSession().createQuery(hql, Object[].class)
                          .getResultList()
                          .stream()
                          .map(array -> {
                              Map<String, Object> map = new HashMap<>();
                              map.put("checkoutDate", array[0]);
                              map.put("employeeId", array[1]);
                              map.put("checkoutId", array[2]);
                              map.put("productId", array[3]);
                              map.put("numberOfCheckout", array[4]);
                              map.put("checkoutPrice", array[5]);
                              map.put("checkoutTotalPrice", array[6]);
                              return map;
                          })
                          .collect(Collectors.toList());
        } catch (Exception e) {
            logger.severe("獲取結帳總表失敗: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public String getLastCheckoutId() {
        try {
            String hql = "select c.checkoutId from CheckoutBean c order by c.checkoutId desc";
            List<String> results = getCurrentSession().createQuery(hql, String.class).setMaxResults(1).list();
            return results.isEmpty() ? "C00000000" : results.get(0);
        } catch (Exception e) {
            logger.severe("獲取最新結帳ID失敗: " + e.getMessage());
            return "C00000000";
        }
    }

    @Transactional(readOnly = true)
    public List<EmpBean> getAllEmployees() {
        try {
            return getCurrentSession().createQuery("from EmpBean", EmpBean.class).list();
        } catch (Exception e) {
            logger.severe("獲取所有員工ID失敗: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<ProductBean> getProductNamesByCategory(String category) {
        try {
            String hql = "from ProductBean where productCategory = :category";
            return getCurrentSession().createQuery(hql, ProductBean.class)
                          .setParameter("category", category)
                          .list();
        } catch (Exception e) {
            logger.severe("獲取產品列表失敗: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional
    public boolean insertCheckoutWithDetails(CheckoutBean checkout, List<CheckoutDetailsBean> details) {
        try {
            getCurrentSession().save(checkout);
            for (CheckoutDetailsBean detail : details) {
                detail.setCheckoutId(checkout.getCheckoutId());
                getCurrentSession().save(detail);
            }
            return true;
        } catch (Exception e) {
            logger.severe("插入結帳記錄和明細失敗: " + e.getMessage());
            throw new RuntimeException("插入結帳記錄和明細失敗", e);
        }
    }

    @Transactional
    public void deleteCheckoutAndDetails(String checkoutId) {
        try {
            CheckoutBean checkout = getCurrentSession().get(CheckoutBean.class, checkoutId);
            if (checkout != null) {
                getCurrentSession().delete(checkout); // 這將級聯刪除相關的結帳明細
            }
        } catch (Exception e) {
            logger.severe("刪除結帳記錄及其相關明細失敗: " + e.getMessage());
            throw new RuntimeException("刪除結帳記錄及其相關明細失敗", e);
        }
    }

    @Transactional
    public void updateTotalAndBonus(String checkoutId, BigDecimal totalAmount, int bonusPoints) {
        try {
            CheckoutBean checkout = getCurrentSession().get(CheckoutBean.class, checkoutId);
            if (checkout != null) {
                checkout.setCheckoutTotalPrice(totalAmount.intValue());
                checkout.setBonusPoints(bonusPoints);
                getCurrentSession().update(checkout);
            }
        } catch (Exception e) {
            logger.severe("更新總金額和紅利點數失敗: " + e.getMessage());
            throw new RuntimeException("更新總金額和紅利點數失敗", e);
        }
    }
}