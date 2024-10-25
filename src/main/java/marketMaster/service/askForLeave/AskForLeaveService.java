package marketMaster.service.askForLeave;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import marketMaster.bean.askForLeave.AskForLeaveBean;
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

	public List<AskForLeaveBean> findAllAskForLeaves() {
		return aslRepo.findAll();
	}

	public Page<AskForLeaveBean> findAslByEmpId(String employeeId, Integer pageNum) {
		Pageable pgb = PageRequest.of(pageNum - 1, 10, Sort.Direction.DESC, "leaveId");
		return aslRepo.findByEmpBeanEmployeeId(employeeId, pgb);
	}

//	public Page<AskForLeaveBean> findAslData(String employeeId, Integer pageNum, String searchTerm,
//			LocalDateTime startTime, LocalDateTime endTime, String leaveCategory, String approvedStatus) {
//		Pageable pageable = PageRequest.of(pageNum - 1, 10, Sort.Direction.DESC, "leaveId");
//		
//		Specification<AskForLeaveBean> spec = Specification.where(AskForLeaveSpecifications.filterByEmpId(employeeId))
//				.and(AskForLeaveSpecifications.filterByStartTime(startTime))
//				.and(AskForLeaveSpecifications.filterByEndTime(endTime))
//				.and(AskForLeaveSpecifications.filterByLeaveCategory(leaveCategory))
//				.and(AskForLeaveSpecifications.filterByApprovedStatus(approvedStatus));
//		
//		Page<AskForLeaveBean> result = aslRepo.findAll(spec, pageable);
//	
//
//		return result;
//	}

	public Page<AskForLeaveBean> filterFindAsl(String employeeId, LocalDateTime startTime, LocalDateTime endTime,
	        String leaveCategory, String approvedStatus, int pageNum) {

	    Pageable pageable = PageRequest.of(pageNum - 1, 10, Sort.Direction.DESC, "leaveId");
	    System.out.println("startTime="+startTime);
	    
	    Specification<AskForLeaveBean> spec = Specification.where(AskForLeaveSpecifications.filterByEmpId(employeeId))
	            .and(AskForLeaveSpecifications.filterByStartTime(startTime))
	            .and(AskForLeaveSpecifications.filterByEndTime(endTime))
	            .and(AskForLeaveSpecifications.filterByLeaveCategory(leaveCategory))
	            .and(AskForLeaveSpecifications.filterByApprovedStatus(approvedStatus));

	    Page<AskForLeaveBean> result = aslRepo.findAll(spec, pageable);
	   
	    return result;
	}


	public AskForLeaveBean findAslById(String id) {
		Optional<AskForLeaveBean> optional = aslRepo.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Transactional
	public AskForLeaveBean saveAsl(AskForLeaveBean askForLeave) {
		return aslRepo.save(askForLeave);

	}

	public void deleteAsl(String id) {
		aslRepo.deleteById(id);

	}

	@Transactional
	public AskForLeaveBean updateAsl(String id, AskForLeaveBean newAskForLeave) {
		Optional<AskForLeaveBean> optional = aslRepo.findById(id);
		if (optional.isPresent()) {
			AskForLeaveBean afl = optional.get();

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
			return "L00001";
		}
		int nextId = Integer.parseInt(currentLeaveId.replace("L", "")) + 1;

		return String.format("L%05d", nextId);
	}

	public boolean isLeaveTimeOverlapping(String employeeId, LocalDateTime newStartTime, LocalDateTime newEndTime) {
		List<AskForLeaveBean> existingLeaves = aslRepo.findByEmpBeanEmployeeId(employeeId);

		for (AskForLeaveBean leave : existingLeaves) {
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

	public Page<AskForLeaveBean> findAllByRank(String employeeId, Integer pageNum) {
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

		Page<AskForLeaveBean> leavePage;
		if (limits == maxlimit) {
			leavePage = aslRepo.findByRankLimitsOfAuthorityLessThanEqual(limits, pgb);
		} else {
			leavePage = aslRepo.findByRankLimitsOfAuthorityLessThan(limits, pgb);
		}
		return leavePage;
	}

	@Transactional
	public void approveLeave(String leaveId) {
		AskForLeaveBean leave = aslRepo.findById(leaveId).orElseThrow(() -> new RuntimeException("找不到指定的請假記錄"));
		leave.setApprovedStatus("已批准");
		aslRepo.save(leave);
	}

	@Transactional
	public void rejectLeave(String leaveId, String rejectionReason) {
		AskForLeaveBean leave = aslRepo.findById(leaveId).orElseThrow(() -> new RuntimeException("找不到指定的請假記錄"));
		leave.setApprovedStatus("已退簽");
		leave.setRejectionReason(rejectionReason);
		aslRepo.save(leave);
	}

}
