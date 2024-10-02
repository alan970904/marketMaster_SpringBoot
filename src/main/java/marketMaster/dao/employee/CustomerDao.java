package marketMaster.dao.employee;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import marketMaster.bean.employee.CustomerBean;
import marketMaster.exception.DataAccessException;

@Repository
public class CustomerDao {

	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

    public boolean addCustomer(CustomerBean customer) throws DataAccessException {
        try {
            getSession().save(customer);
            return true;
        } catch (Exception e) {
            throw new DataAccessException("新增會員失敗", e);
        }
    }

    public CustomerBean getCustomer(String customerTel) throws DataAccessException {
        try {
            return getSession().get(CustomerBean.class, customerTel);
        } catch (Exception e) {
            throw new DataAccessException("獲取會員詳情失敗", e);
        }
    }

    public List<CustomerBean> getAllCustomers() throws DataAccessException {
        try {
            return getSession().createQuery("from CustomerBean", CustomerBean.class).list();
        } catch (Exception e) {
            throw new DataAccessException("獲取所有會員資訊失敗", e);
        }
    }

    public boolean updateCustomer(CustomerBean customer, String originalTel) throws DataAccessException {
        try {
            CustomerBean existingCustomer = getSession().get(CustomerBean.class, originalTel);
            if (existingCustomer != null) {
                // 如果手機號碼有變更，需要先刪除舊記錄再插入新記錄
                if (!originalTel.equals(customer.getCustomerTel())) {
                    getSession().delete(existingCustomer);
                    getSession().save(customer);
                } else {
                    existingCustomer.setCustomerName(customer.getCustomerName());
                    existingCustomer.setCustomerEmail(customer.getCustomerEmail());
                    getSession().update(existingCustomer);
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new DataAccessException("更新會員失敗", e);
        }
    }

    public boolean deleteCustomer(String customerTel) throws DataAccessException {
        try {
            CustomerBean customer = getSession().get(CustomerBean.class, customerTel);
            if (customer != null) {
                getSession().delete(customer);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new DataAccessException("刪除會員失敗", e);
        }
    }
}