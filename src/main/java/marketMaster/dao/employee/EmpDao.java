package marketMaster.dao.employee;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import marketMaster.bean.employee.EmpBean;
import marketMaster.bean.employee.RankLevelBean;
import marketMaster.exception.DataAccessException;
import marketMaster.viewModel.EmployeeViewModel;

@Repository
public class EmpDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public EmpBean validateEmployee(String employeeId, String password) throws DataAccessException {
        try {
            Query<EmpBean> query = getSession().createQuery("FROM EmpBean WHERE employeeId = :id AND password = :pwd", EmpBean.class);
            query.setParameter("id", employeeId);
            query.setParameter("pwd", password);
            return query.uniqueResult();
        } catch (Exception e) {
            throw new DataAccessException("驗證員工失敗", e);
        }
    }

    public boolean isFirstLogin(String employeeId) throws DataAccessException {
        try {
            EmpBean emp = getSession().get(EmpBean.class, employeeId);
            return emp != null && emp.isFirstLogin();
        } catch (Exception e) {
            throw new DataAccessException("檢查首次登入狀態失敗", e);
        }
    }

    public boolean updatePassword(String employeeId, String newPassword) throws DataAccessException {
        try {
            EmpBean emp = getSession().get(EmpBean.class, employeeId);
            if (emp != null) {
                emp.setPassword(newPassword);
                emp.setFirstLogin(false);
                getSession().update(emp);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new DataAccessException("更新密碼失敗", e);
        }
    }
    
    public boolean addEmployee(EmpBean emp) throws DataAccessException {
        try {
            getSession().save(emp);
            return true;
        } catch (Exception e) {
            throw new DataAccessException("添加員工失敗", e);
        }
    }

    public boolean deleteEmployee(String employeeId) throws DataAccessException {
        try {
            EmpBean emp = getSession().get(EmpBean.class, employeeId);
            if (emp != null) {
                getSession().delete(emp);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new DataAccessException("刪除員工失敗", e);
        }
    }

    public boolean updateEmployee(EmpBean emp) throws DataAccessException {
        try {
        	EmpBean existingEmp = getSession().get(EmpBean.class, emp.getEmployeeId());
            if (existingEmp != null) {
                // 更新現有實體的屬性
                existingEmp.setEmployeeName(emp.getEmployeeName());
                existingEmp.setEmployeeTel(emp.getEmployeeTel());
                existingEmp.setEmployeeIdcard(emp.getEmployeeIdcard());
                existingEmp.setEmployeeEmail(emp.getEmployeeEmail());
                existingEmp.setPassword(emp.getPassword());
                existingEmp.setPositionId(emp.getPositionId());
                existingEmp.setHiredate(emp.getHiredate());
                existingEmp.setResigndate(emp.getResigndate());
                
                // 使用 merge 方法更新實體
                getSession().merge(existingEmp);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new DataAccessException("更新員工失敗", e);
        }
    }

    public EmpBean getEmployee(String employeeId) throws DataAccessException {
        try {
            return getSession().get(EmpBean.class, employeeId);
        } catch (Exception e) {
            throw new DataAccessException("獲取員工詳情失敗", e);
        }
    }

    public List<EmpBean> getAllEmployees(boolean showAll) throws DataAccessException {
        try {
            String hql = showAll ? "FROM EmpBean" : "FROM EmpBean WHERE resigndate IS NULL";
            return getSession().createQuery(hql, EmpBean.class).list();
        } catch (Exception e) {
            throw new DataAccessException("獲取所有員工資訊失敗", e);
        }
    }

    public List<EmpBean> searchEmployees(String searchName, boolean showAll) throws DataAccessException {
        try {
            String hql = showAll
                ? "FROM EmpBean WHERE employeeName LIKE :name"
                : "FROM EmpBean WHERE employeeName LIKE :name AND resigndate IS NULL";
            Query<EmpBean> query = getSession().createQuery(hql, EmpBean.class);
            query.setParameter("name", "%" + searchName + "%");
            return query.list();
        } catch (Exception e) {
            throw new DataAccessException("查詢員工失敗", e);
        }
    }

    public List<RankLevelBean> getRankList() throws DataAccessException {
        try {
            String hql = "FROM RankLevelBean";
            List<RankLevelBean> rankList = getSession().createQuery(hql, RankLevelBean.class).list();
            
            for (RankLevelBean rank : rankList) {
                String countHql = "SELECT COUNT(e) FROM EmpBean e WHERE e.positionId = :positionId";
                Long totalCount = getSession().createQuery(countHql, Long.class)
                    .setParameter("positionId", rank.getPositionId())
                    .uniqueResult();
                
                String activeCountHql = "SELECT COUNT(e) FROM EmpBean e WHERE e.positionId = :positionId AND e.resigndate IS NULL";
                Long activeCount = getSession().createQuery(activeCountHql, Long.class)
                    .setParameter("positionId", rank.getPositionId())
                    .uniqueResult();
                
                rank.setTotalEmployeeCount(totalCount.intValue());
                rank.setActiveEmployeeCount(activeCount.intValue());
            }
            
            return rankList;
        } catch (Exception e) {
            throw new DataAccessException("獲取職級列表失敗", e);
        }
    }

    public EmployeeViewModel getEmployeeViewModel(String employeeId) throws DataAccessException {
        try {
            EmpBean emp = getSession().get(EmpBean.class, employeeId);
            if (emp == null) {
                throw new DataAccessException("找不到員工：" + employeeId);
            }
            
            return new EmployeeViewModel(
                emp.getEmployeeId(),
                emp.getEmployeeName(),
                emp.getEmployeeTel(),
                emp.getEmployeeIdcard(),
                emp.getEmployeeEmail(),
                emp.getRankLevel().getPositionName(),
                emp.getRankLevel().getSalaryLevel(),
                emp.getHiredate(),
                emp.getResigndate(),
                emp.getPassword(),
                emp.getPositionId()
            );
        } catch (Exception e) {
            throw new DataAccessException("獲取員工視圖失敗", e);
        }
    }

    public String getLastEmployeeId() throws DataAccessException {
        try {
            String hql = "SELECT e.employeeId FROM EmpBean e ORDER BY e.employeeId DESC";
            List<String> result = getSession().createQuery(hql, String.class).setMaxResults(1).list();
            return result.isEmpty() ? "E001" : result.get(0);
        } catch (Exception e) {
            throw new DataAccessException("獲取最後一個員工ID失敗", e);
        }
    }
}