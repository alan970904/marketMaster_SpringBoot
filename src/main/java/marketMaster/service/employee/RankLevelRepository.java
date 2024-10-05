package marketMaster.service.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import marketMaster.bean.employee.RankLevelBean;

@Repository
public interface RankLevelRepository extends JpaRepository<RankLevelBean, String> {

}
