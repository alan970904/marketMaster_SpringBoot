package marketMaster.service.askForLeave;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import marketMaster.bean.askForLeave.LeaveCategoryBean;
import marketMaster.bean.askForLeave.LeaveRecordBean;

public interface LeaveCategoryRepository extends JpaRepository<LeaveCategoryBean, Integer> {

	

}
