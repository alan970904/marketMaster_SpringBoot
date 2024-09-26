package marketMaster.service.restock;

import marketMaster.bean.restock.RestockBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RestockRepository extends JpaRepository<RestockBean,String> {
    // 查詢最新的 Restock ID 並排序
    @Query("SELECT r FROM RestockBean r WHERE r.restockId LIKE :restockIdPattern ORDER BY r.restockId DESC")
    Optional<RestockBean> findLatestRestockByDate(@Param("restockIdPattern") String restockIdPattern);

}
