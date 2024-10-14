    package marketMaster.service.restock;

    import marketMaster.DTO.restock.restock.RestockDTO;
    import marketMaster.DTO.restock.restock.RestockDetailDTO;
    import marketMaster.bean.restock.RestocksBean;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Modifying;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;
    import org.springframework.transaction.annotation.Transactional;

    import java.time.LocalDate;
    import java.util.List;
    import java.util.Optional;

    public interface RestocksRepository extends JpaRepository<RestocksBean, String> {
//        // 查詢最新的 Restock ID 並排序 mysql
//        @Query(value = "SELECT * FROM restocks WHERE restock_id LIKE CONCAT(:restockIdPattern, '%') ORDER BY restock_id DESC LIMIT 1", nativeQuery = true)
//        Optional<RestocksBean> findLatestRestockByDate(@Param("restockIdPattern") String restockIdPattern);

////        sqlServer
        @Query(value = "SELECT TOP 1 * FROM restocks WHERE restock_id LIKE :restockIdPattern + '%' ORDER BY restock_id DESC", nativeQuery = true)
        Optional<RestocksBean> findLatestRestockByDate(@Param("restockIdPattern") String restockIdPattern);

        //找到所有進貨Id並計算內明細的家總金額
        @Query("SELECT new marketMaster.DTO.restock.restock.RestockDTO(s.restockId, s.employee.employeeId, s.employee.employeeName, s.restockDate, s.restockTotalPrice) FROM RestocksBean s")
        Page<RestockDTO> findAllRestocks(Pageable pageable);

        //更新進貨單總金額
        @Transactional
        @Modifying
        @Query("UPDATE RestocksBean r SET r.restockTotalPrice=:restockTotalPrice WHERE r.restockId=:restockId")
        void updateRestocksTotalPrice(@Param("restockId")String restockId,@Param("restockTotalPrice")int restockTotalPrice);


        //根據日期區間做查詢
        @Query("SELECT new marketMaster.DTO.restock.restock.RestockDTO(s.restockId, s.employee.employeeId, s.employee.employeeName, s.restockDate, s.restockTotalPrice) FROM RestocksBean s WHERE s.restockDate BETWEEN :startDate AND :endDate")
        Page<RestockDTO> findRestockDetailByRestockDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate")LocalDate endDate,Pageable pageable);
    }
