package marketMaster.service.restock;

import marketMaster.DTO.restock.restock.RestockDTO;
import marketMaster.bean.restock.RestocksBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestocksRepository extends JpaRepository<RestocksBean, String> {
    // 查詢最新的 Restock ID 並排序
    @Query(value = "SELECT * FROM restocks WHERE restock_id LIKE CONCAT(:restockIdPattern, '%') ORDER BY restock_id DESC LIMIT 1", nativeQuery = true)
    Optional<RestocksBean> findLatestRestockByDate(@Param("restockIdPattern") String restockIdPattern);

    //找到所有進貨Id並計算內明細的家總金額
    @Query("SELECT new marketMaster.DTO.restock.restock.RestockDTO( s.restockId,s.employee.employeeId,s.employee.employeeName,s.restockDate,s.restockTotalPrice)from RestocksBean s ")
    List<RestockDTO> findAllRestocks();
}