package marketMaster.service.askForLeave;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import marketMaster.bean.askForLeave.LeaveCategory;

@Service
public class LeaveCategoryService {
	
	@Autowired
	private LeaveCategoryRepository leaveCategoryRepo;
	
	public LeaveCategory getLeaveCategoryById(Integer id) {
        return leaveCategoryRepo.findById(id).orElse(null); 
    }
	
	public List<LeaveCategory> findAllLeaveCategories() {
        return leaveCategoryRepo.findAll();
    }

}
