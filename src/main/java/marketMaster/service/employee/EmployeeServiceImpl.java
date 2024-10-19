package marketMaster.service.employee;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import marketMaster.annotation.NotificationTrigger;
import marketMaster.bean.employee.EmpBean;
import marketMaster.bean.employee.RankLevelBean;
import marketMaster.exception.EmpDataAccessException;
import marketMaster.service.notification.NotificationService;
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
	
	@Autowired
	private EmailServiceImpl emailService;
	
	@Autowired
	private NotificationService notificationService;
	
	@Value("${upload.path}")
	private String uploadPath;
	
    @Override
	@PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadPath));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
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
	@NotificationTrigger(event = "NEW_EMPLOYEE", roles = {"經理", "主管"})
	public boolean addEmployee(EmpBean emp, MultipartFile file) {
        try {
            if (file != null && !file.isEmpty()) {
                String fileName = saveImage(file);
                emp.setImagePath(fileName);
            }
            
            emp.setHiredate(LocalDate.now());
            emp.setPassword(passwordEncoder.encode("0000"));
            emp.setFirstLogin(true);
            
            EmpBean savedEmployee = employeeRepository.save(emp);
            
            if (savedEmployee != null) {
				notificationService.sendNotificationByEvent("NEW_EMPLOYEE", new String[] {"經理", "主管"});
				return true;
            } else {
				return false;
			}
            
        } catch (Exception e) {
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
	public Page<EmpBean> searchEmployees(String searchName, boolean showAll, Pageable pageable) {
		return showAll
				? employeeRepository.findByEmployeeNameContaining(searchName, pageable)
				: employeeRepository.findByEmployeeNameContainingAndResigndateIsNull(searchName, pageable);
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
        EmployeeViewModel viewModel = new EmployeeViewModel(
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
        viewModel.setAuthority(emp.getAuthority());
        return viewModel;
	}
	
	@Override
	public String generateNewEmployeeId() {
		List<String> result = employeeRepository.findLastEmployeeId();
		String lastId = result.isEmpty() ? "E000" : result.get(0);
		int numPart = Integer.parseInt(lastId.substring(1)) + 1;
		return String.format("E%03d", numPart);
	}
	
	@Override
	@NotificationTrigger(event = "EMPLOYEE_RESIGNED", roles = {"經理", "主管"})
	public boolean updateEmployee(EmpBean emp, MultipartFile file) throws EmpDataAccessException {
        try {
            // 檢查員工是否存在
            EmpBean existingEmp = employeeRepository.findById(emp.getEmployeeId())
                .orElseThrow(() -> new EmpDataAccessException("員工不存在"));

            boolean isResigning = emp.getResigndate() != null && existingEmp.getResigndate() == null;
            
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
                // 刪除舊圖片
                if (existingEmp.getImagePath() != null) {
                    deleteImage(existingEmp.getImagePath());
                }
                // 保存新圖片
                String fileName = saveImage(file);
                existingEmp.setImagePath(fileName);
            }

            // 保存更新後的員工信息
            EmpBean updateEmployee = employeeRepository.save(existingEmp);
            
            if (updateEmployee != null) {
				if (isResigning) {
					notificationService.sendNotificationByEvent("EMPLOYEE_RESIGNED", new String[]{"經理", "主管"});
				}
				return true;
			} else {
				return false;
			}
            
        } catch (Exception e) {
            throw new EmpDataAccessException("更新員工失敗", e);
        }
    }
	
    private String saveImage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filepath = Paths.get(uploadPath, fileName);
        Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    private void deleteImage(String fileName) {
        try {
            Path filepath = Paths.get(uploadPath, fileName);
            Files.deleteIfExists(filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
	public Resource getEmployeePhoto(String employeeId) {
        EmpBean employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new EmpDataAccessException("員工不存在"));
        
        if (employee.getImagePath() != null) {
            try {
                Path file = Paths.get(uploadPath).resolve(employee.getImagePath());
                Resource resource = new UrlResource(file.toUri());
                if (resource.exists() || resource.isReadable()) {
                    return resource;
                }
            } catch (MalformedURLException e) {
                throw new EmpDataAccessException("無法讀取圖片", e);
            }
        }
        
        return null;
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

	@Override
	public Page<EmpBean> getEmployeeById(String employeeId, Pageable pageable) {
		EmpBean employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmpDataAccessException("找不到員工"));
        return new PageImpl<>(Collections.singletonList(employee), pageable, 1);
	}

	@Override
	public boolean resetPasswordAndSendEmail(String employeeId, String idCardLast4) {
		if (validateEmployeeInfo(employeeId, idCardLast4)) {
			EmpBean employee = getEmployee(employeeId);
			String tempPassword = generateTempPassword();
			updatePassword(employeeId, tempPassword);
			emailService.sendPasswordResetEmail(employee.getEmployeeEmail(), tempPassword);
			return true;
		}
		
		return false;
	}

}
