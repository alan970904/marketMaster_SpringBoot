package marketMaster.service.askForLeave;

import org.springframework.data.jpa.repository.JpaRepository;

import marketMaster.bean.askForLeave.LeaveCategory;

public interface LeaveCategoryRepository extends JpaRepository<LeaveCategory, Integer> {

}
