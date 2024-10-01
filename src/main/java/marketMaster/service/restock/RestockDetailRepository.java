package marketMaster.service.restock;

import marketMaster.DTO.restock.RestockDetailViewDTO;
import marketMaster.bean.restock.RestockDetailsBean;
import marketMaster.bean.restock.RestockDetailsId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RestockDetailRepository extends JpaRepository<RestockDetailsBean, RestockDetailsId> {

    @Query("SELECT new marketMaster.DTO.restock.RestockDetailViewDTO(" +
            "r.restockId, r.employee.employeeId, rd.product.productId, rd.product.productName, rd.product.productCategory, " +
            "rd.numberOfRestock, rd.productPrice, " +
            "r.restockDate, rd.productionDate, rd.dueDate) " +
            "FROM RestockDetailsBean rd " +
            "JOIN rd.restock r " +
            "ORDER BY r.restockId DESC")
    List<RestockDetailViewDTO> getAllRestockDetails();

    @Query("SELECT new marketMaster.DTO.restock.RestockDetailViewDTO(" +
            "r.restockId, r.employee.employeeId, rd.product.productId, rd.product.productName, rd.product.productCategory, " +
            "rd.numberOfRestock, rd.productPrice, " +
            "r.restockDate, rd.productionDate, rd.dueDate) " +
            "FROM RestockDetailsBean rd " +
            "JOIN rd.restock r " +
            "WHERE SUBSTRING(r.restockId, 1, 8) BETWEEN :startDate AND :endDate " +
            "ORDER BY r.restockId DESC")
    List<RestockDetailViewDTO> findRestockDetailsByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);


    @Modifying
    @Transactional
    @Query("UPDATE RestockDetailsBean rd SET rd.numberOfRestock = :numberOfRestock, rd.productPrice = :productPrice,rd.productionDate=:productionDate,rd.dueDate = :dueDate " +
            "WHERE rd.id.restockId = :restockId AND rd.id.productId = :productId")
    void updateRestockDetail(@Param("restockId") String restockId,
                             @Param("productId") String productId,
                             @Param("numberOfRestock") int numberOfRestock,
                             @Param("productPrice") int productPrice,
                             @Param("productionDate")LocalDate productionDate,
                             @Param("dueDate") LocalDate dueDate);
}
