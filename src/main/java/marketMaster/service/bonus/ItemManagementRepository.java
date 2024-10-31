package marketMaster.service.bonus;

import marketMaster.bean.bonus.ItemManagementBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
//import java.util.Optional;

@Repository
public interface ItemManagementRepository extends JpaRepository<ItemManagementBean,String> {
// 基本查詢方法（已經由 JpaRepository 提供）
// findById() // findAll() // save() // deleteById()
// 查詢啟用狀態的商品
    List<ItemManagementBean> findByActiveTrue();
    // 根據商品類別查詢
    @Query("SELECT i FROM ItemManagementBean i WHERE i.product.productCategory = :category")
    List<ItemManagementBean> findByCategory(@Param("category") String category);
    // 查詢有效期內的商品
    @Query("SELECT i FROM ItemManagementBean i WHERE i.startDate <= :currentDate AND i.endDate >= :currentDate")
    List<ItemManagementBean> findValidItems(@Param("currentDate") LocalDate currentDate);
    // 查詢庫存不足的商品（小於等於安全庫存量）
    @Query("SELECT i FROM ItemManagementBean i WHERE i.itemMaximum <= :safeStock")
    List<ItemManagementBean> findLowStockItems(@Param("safeStock") int safeStock);


    //以下為紅利兌換BonusExchange使用
    // 查詢會員可兌換的商品（根據點數和有效期）
    @Query("SELECT i FROM ItemManagementBean i " +
            "WHERE i.active = true " +
            "AND i.startDate <= :currentDate " +
            "AND i.endDate >= :currentDate " +
            "AND i.itemPoints <= :customerPoints " +
            "AND i.itemMaximum > 0")
    List<ItemManagementBean> findExchangeableItems(
            @Param("currentDate") LocalDate currentDate,
            @Param("customerPoints") int customerPoints
    );

    // 根據商品類別查詢有效的可兌換商品
    @Query("SELECT i FROM ItemManagementBean i " +
            "WHERE i.active = true " +
            "AND i.startDate <= :currentDate " +
            "AND i.endDate >= :currentDate " +
            "AND i.product.productCategory = :category " +
            "AND i.itemMaximum > 0")
    List<ItemManagementBean> findExchangeableItemsByCategory(
            @Param("category") String category,
            @Param("currentDate") LocalDate currentDate
    );

    // 更新可兌換數量
    @Transactional
    @Modifying
    @Query("UPDATE ItemManagementBean i SET i.itemMaximum = :newMaximum " +
            "WHERE i.itemId = :itemId")
    void updateItemMaximum(
            @Param("itemId") String itemId,
            @Param("newMaximum") int newMaximum
    );
}