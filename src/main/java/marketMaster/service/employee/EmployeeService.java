package marketMaster.service.employee;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import marketMaster.DTO.employee.MonthlyStatisticsDTO;
import marketMaster.bean.employee.EmpBean;
import marketMaster.bean.employee.RankLevelBean;
import marketMaster.exception.EmpDataAccessException;
import marketMaster.viewModel.EmployeeViewModel;

public interface EmployeeService {

	void init();

	EmpBean login(String employeeId, String rawPassword);

	boolean isFirstLogin(String employeeId);

	boolean updatePassword(String employeeId, String newPassword);

	boolean addEmployee(EmpBean emp, MultipartFile file);

	boolean deleteEmployee(String employeeId);

	EmpBean getEmployee(String employeeId);

	Page<EmpBean> getAllEmployees(boolean showAll, Pageable pageable);

	Page<EmpBean> searchEmployees(String searchName, boolean showAll, Pageable pageable);

	List<RankLevelBean> getRankList();

	EmployeeViewModel getEmployeeViewModel(String employeeId);

	String generateNewEmployeeId();

	boolean updateEmployee(EmpBean emp, MultipartFile file) throws EmpDataAccessException;

	Resource getEmployeePhoto(String employeeId);

	List<RankLevelBean> getAllPositions();

	boolean validateEmployeeInfo(String employeeId, String idCardLast4);

	String generateTempPassword();

	List<EmpBean> findAllEmp();

	Page<EmpBean> getEmployeeById(String employeeId, Pageable pageable);

	boolean resetPasswordAndSendEmail(String employeeId, String idCardLast4);
	
	// 員工圖表
	List<MonthlyStatisticsDTO> getMonthlyEmployeesStatistics();

}
