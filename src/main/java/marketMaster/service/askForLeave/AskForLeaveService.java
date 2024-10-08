package marketMaster.service.askForLeave;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import marketMaster.bean.askForLeave.AskForLeave;

@Service
public class AskForLeaveService {

	@Autowired
	private AskForLeaveRepository aslRepo;
	


	public List<AskForLeave> findAllAskForLeaves() {
		return aslRepo.findAll();
	}
	
	public Page<AskForLeave> findAslPage(Integer pageNum){
		Pageable pgb = PageRequest.of(pageNum-1, 10, Sort.Direction.DESC ,"leaveId");
		Page<AskForLeave> page = aslRepo.findAll(pgb);
		return page;
	}

	public List<AskForLeave> findAslByEmpId(String employeeId) {
        return aslRepo.findByEmpBeanEmployeeId(employeeId);
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
	        afl.setLeaveType(newAskForLeave.getLeaveType()); 
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
	 
	 
	
}
