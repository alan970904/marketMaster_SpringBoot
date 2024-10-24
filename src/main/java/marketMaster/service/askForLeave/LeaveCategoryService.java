package marketMaster.service.askForLeave;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import marketMaster.bean.askForLeave.LeaveCategoryBean;

@Service
public class LeaveCategoryService {
	
	@Autowired
	private LeaveCategoryRepository leaveCategoryRepo;
	
	public LeaveCategoryBean getLeaveCategoryById(Integer id) {
        return leaveCategoryRepo.findById(id).orElse(null); 
    }
	
	public List<LeaveCategoryBean> findAllLeaveCategories() {
        return leaveCategoryRepo.findAll();
    }

}
