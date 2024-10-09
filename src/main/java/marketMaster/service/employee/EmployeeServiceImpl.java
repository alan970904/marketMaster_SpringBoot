package marketMaster.service.employee;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import marketMaster.bean.employee.EmpBean;
import marketMaster.bean.employee.RankLevelBean;
import marketMaster.exception.EmpDataAccessException;
import marketMaster.viewModel.EmployeeViewModel;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService{

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
    @PersistenceContext
    private EntityManager entityManager;
	
	@Autowired
	private RankLevelRepository rankLevelRepository;
	
	private final Path root = Paths.get("uploads");
	
	public EmployeeServiceImpl() {
		
		try {
			Files.createDirectories(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public EmpBean login(String employeeId, String rawPassword) {
	    EmpBean employee = employeeRepository.findByEmployeeId(employeeId)
	        .orElseThrow(() -> new EmpDataAccessException("驗證員工失敗"));
	    
	    if (passwordEncoder.matches(rawPassword, employee.getPassword())) {
	        return employee;
	    } else if (rawPassword.equals(employee.getPassword())) {
	        // 如果明文密碼匹配，更新為加密密碼
	        employee.setPassword(passwordEncoder.encode(rawPassword));
	        employeeRepository.save(employee);
	        return employee;
	    } else {
	        throw new EmpDataAccessException("驗證員工失敗");
	    }
	}
	
	@Override
	public boolean isFirstLogin(String employeeId) {
		return employeeRepository.isFirstLogin(employeeId);
	}
	
	@Override
	public boolean updatePassword(String employeeId, String newPassword) {
	    try {
	        EmpBean employee = employeeRepository.findById(employeeId).orElse(null);
	        if (employee != null) {
	            employee.setPassword(passwordEncoder.encode(newPassword));
	            employee.setFirstLogin(false);
	            employeeRepository.save(employee);
	            return true;
	        }
	        return false;
	    } catch (Exception e) {
	        return false;
	    }
	}
	
	@Override
	public boolean addEmployee(EmpBean emp, MultipartFile file) {
        try {
            if (file != null && !file.isEmpty()) {
                String imagePath = saveImage(file);
                emp.setImagePath(imagePath);
            }
            
            emp.setHiredate(LocalDate.now());
            emp.setPassword(passwordEncoder.encode("0000"));
            emp.setFirstLogin(true);
            
            employeeRepository.save(emp);
            return true;
        } catch (Exception e) {
            // 記錄錯誤
            e.printStackTrace();
            return false;
        }
    }
	
	@Override
	public boolean deleteEmployee(String employeeId) {
		employeeRepository.deleteById(employeeId);
		return true;
	}
	
	@Override
	public EmpBean getEmployee(String employeeId) {
		return employeeRepository.findById(employeeId)
				.orElseThrow(() -> new EmpDataAccessException("找不到員工"));
	}
	
	@Override
	public Page<EmpBean> getAllEmployees(boolean showAll, Pageable pageable) {
		return showAll ? employeeRepository.findAll(pageable) : employeeRepository.findByResigndateIsNull(pageable);
	}
	
	@Override
	public List<EmpBean> searchEmployees(String searchName, boolean showAll) {
		return showAll
				? employeeRepository.findByEmployeeNameContaining(searchName)
				: employeeRepository.findByEmployeeNameContainingAndResigndateIsNull(searchName);
	}
	
	@Override
	public List<RankLevelBean> getRankList() {
		List<RankLevelBean> rankList = rankLevelRepository.findAll();
        for (RankLevelBean rank : rankList) {
        	int totalCount = employeeRepository.countByPositionId(rank.getPositionId());
        	int activeCount = employeeRepository.countByPositionIdAndResigndateIsNull(rank.getPositionId());
            rank.setTotalEmployeeCount((int) totalCount);
            rank.setActiveEmployeeCount((int) activeCount);
        }
        return rankList;
	}
	
	@Override
	public EmployeeViewModel getEmployeeViewModel(String employeeId) {
        EmpBean emp = getEmployee(employeeId);
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
            emp.getPositionId(),
            emp.getImagePath()
        );
	}
	
	@Override
	public String generateNewEmployeeId() {
		List<String> result = employeeRepository.findLastEmployeeId();
		String lastId = result.isEmpty() ? "E000" : result.get(0);
		int numPart = Integer.parseInt(lastId.substring(1)) + 1;
		return String.format("E%03d", numPart);
	}
	
    @Override
	public boolean updateEmployee(EmpBean emp, MultipartFile file) throws EmpDataAccessException {
        try {
            // 檢查員工是否存在
            EmpBean existingEmp = employeeRepository.findById(emp.getEmployeeId())
                .orElseThrow(() -> new EmpDataAccessException("員工不存在"));

            // 更新員工信息
            existingEmp.setEmployeeName(emp.getEmployeeName());
            existingEmp.setEmployeeTel(emp.getEmployeeTel());
            existingEmp.setEmployeeIdcard(emp.getEmployeeIdcard());
            existingEmp.setEmployeeEmail(emp.getEmployeeEmail());
            existingEmp.setPositionId(emp.getPositionId());
            existingEmp.setHiredate(emp.getHiredate());
            existingEmp.setResigndate(emp.getResigndate());

            // 如果有新的圖片上傳，處理圖片
            if (file != null && !file.isEmpty()) {
                String imagePath = saveImage(file);
                existingEmp.setImagePath(imagePath);
            }

            // 保存更新後的員工信息
            employeeRepository.save(existingEmp);
            return true;
        } catch (EmpDataAccessException e) {
            throw e;
        } catch (Exception e) {
            throw new EmpDataAccessException("更新員工失敗", e);
        }
    }
	
	@Override
	public String saveImage(MultipartFile file) {
		if (file.isEmpty()) {
			throw new EmpDataAccessException("圖片為空");
		}
		String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
		Path path = this.root.resolve(filename);
		try {
			Files.copy(file.getInputStream(), path);
		} catch (IOException e) {
			throw new EmpDataAccessException("無法保存圖片", e);
		}
		return path.toString();
	}

	@Override
	public List<RankLevelBean> getAllPositions() {
	    return rankLevelRepository.findAll();
	}

    @Override
	public boolean validateEmployeeInfo(String employeeId, String idCardLast4) {
        EmpBean employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee != null) {
            String fullIdCard = employee.getEmployeeIdcard();
            return fullIdCard.endsWith(idCardLast4);
        }
        return false;
    }
	
    @Override
	public String generateTempPassword() {
        // 生成4位隨機數字密碼
        Random random = new Random();
        int number = 1000 + random.nextInt(9000);
        return String.valueOf(number);
    }
    
    @Override
	public List<EmpBean> findAllEmp() {
        return employeeRepository.findAll();
    }

}