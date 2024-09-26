package marketMaster.service.restock;

import marketMaster.bean.restock.RestockDetailsBean;
import marketMaster.bean.restock.RestockDetailsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestockDetailRepository extends JpaRepository<RestockDetailsBean, RestockDetailsId> {



}
