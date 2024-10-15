package marketMaster.service.askForLeave;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import marketMaster.bean.askForLeave.AskForLeave;
import marketMaster.bean.employee.EmpBean;
import marketMaster.bean.employee.RankLevelBean;
import marketMaster.service.employee.EmployeeRepository;
import marketMaster.service.employee.RankLevelRepository;

@Service
public class AskForLeaveService {

	@Autowired
	private AskForLeaveRepository aslRepo;

	@Autowired
	private RankLevelRepository rankLevelRepo;

	@Autowired
	private EmployeeRepository empRepo;

	public List<AskForLeave> findAllAskForLeaves() {
		return aslRepo.findAll();
	}

	public Page<AskForLeave> findAslPage(Integer pageNum) {
		Pageable pgb = PageRequest.of(pageNum - 1, 10, Sort.Direction.DESC, "leaveId");
		Page<AskForLeave> page = aslRepo.findAll(pgb);
		return page;
	}

	public Page<AskForLeave> findAslByEmpId(String employeeId, Integer pageNum) {
		Pageable pgb = PageRequest.of(pageNum - 1, 10, Sort.Direction.DESC, "leaveId");
		return aslRepo.findByEmpBeanEmployeeId(employeeId, pgb);
	}

	public AskForLeave findAslById(String id) {
		Optional<AskForLeave> optional = aslRepo.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Transactional
	public AskForLeave saveAsl(AskForLeave askForLeave) {
		return aslRepo.save(askForLeave);

	}

	public void deleteAsl(String id) {
		aslRepo.deleteById(id);

	}

	@Transactional
	public AskForLeave updateAsl(String id, AskForLeave newAskForLeave) {
		Optional<AskForLeave> optional = aslRepo.findById(id);
		if (optional.isPresent()) {
			AskForLeave afl = optional.get();

			afl.setStarTime(newAskForLeave.getStarTime());
			afl.setEndTime(newAskForLeave.getEndTime());
			afl.setLeaveCategory(newAskForLeave.getLeaveCategory());
			afl.setReasonLeave(newAskForLeave.getReasonLeave());
			afl.setProofImage(newAskForLeave.getProofImage());
			afl.setApprovedStatus(newAskForLeave.getApprovedStatus());
			afl.setEmpBean(newAskForLeave.getEmpBean());

			return aslRepo.save(afl);
		}
		return null;
	}

	public String generateId() {
		String currentLeaveId = aslRepo.findMaxLeaveId();

		if (currentLeaveId == null) {
			return "L0001";
		}
		int nextId = Integer.parseInt(currentLeaveId.replace("L", "")) + 1;

		return String.format("L%04d", nextId);
	}

	public boolean isLeaveTimeOverlapping(String employeeId, LocalDateTime newStartTime, LocalDateTime newEndTime) {
		List<AskForLeave> existingLeaves = aslRepo.findByEmpBeanEmployeeId(employeeId);

		for (AskForLeave leave : existingLeaves) {
			LocalDateTime existingStartTime = leave.getStarTime();
			LocalDateTime existingEndTime = leave.getEndTime();

			if (newStartTime.isBefore(existingEndTime) && newEndTime.isAfter(existingStartTime)) {
				return true;
			}
		}

		return false;
	}

	public MediaType getMediaTypeForFile(byte[] fileData) {
		String fileExtension = getFileExtension(fileData); // 取得副檔名
		return getMediaType(fileExtension); // 根據副檔名返回適當的 MediaType
	}

	private String getFileExtension(byte[] fileData) {
		String fileName = "example.jpg"; // 範例：檔案名稱
		return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
	}

	private MediaType getMediaType(String fileExtension) {
		switch (fileExtension) {
		case "jpg":
		case "jpeg":
			return MediaType.IMAGE_JPEG;
		case "png":
			return MediaType.IMAGE_PNG;
		case "pdf":
			return MediaType.APPLICATION_PDF;
		default:
			return MediaType.APPLICATION_OCTET_STREAM;
		}
	}

	public Page<AskForLeave> findAllByRank(String employeeId, Integer pageNum) {
		Pageable pgb = PageRequest.of(pageNum - 1, 10, Sort.Direction.DESC, "leaveId");
	    Optional<EmpBean> optional = empRepo.findById(employeeId);
	    if (!optional.isPresent()) {
	        return Page.empty();  
	    }
	    EmpBean empBean = optional.get();
	    RankLevelBean rankLevel = empBean.getRankLevel();
	    if (rankLevel == null) {
	        return Page.empty();  
	    }
	    int limits = rankLevel.getLimitsOfAuthority();
	    RankLevelBean maxRank = rankLevelRepo.findTopByOrderByLimitsOfAuthorityDesc();
	    int maxlimit = maxRank.getLimitsOfAuthority();

	    Page<AskForLeave> leavePage;
	    if (limits == maxlimit) {
	        leavePage = aslRepo.findByRankLimitsOfAuthorityLessThanEqual(limits, pgb);
	    } else {
	        leavePage = aslRepo.findByRankLimitsOfAuthorityLessThan(limits, pgb);
	    }
	    return leavePage;
	}
	
	 @Transactional
	    public void approveLeave(String leaveId) {
	        AskForLeave leave = aslRepo.findById(leaveId)
	                .orElseThrow(() -> new RuntimeException("找不到指定的請假記錄"));
	        leave.setApprovedStatus("已批准");
	        aslRepo.save(leave);
	    }

	    @Transactional
	    public void rejectLeave(String leaveId, String rejectionReason) {
	        AskForLeave leave = aslRepo.findById(leaveId)
	                .orElseThrow(() -> new RuntimeException("找不到指定的請假記錄"));
	        leave.setApprovedStatus("已退簽");
	        leave.setRejectionReason(rejectionReason);
	        aslRepo.save(leave);
	    }


}
