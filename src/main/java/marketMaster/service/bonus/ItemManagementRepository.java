package marketMaster.service.bonus;

import marketMaster.bean.bonus.ItemManagementBean;
import marketMaster.bean.product.ProductBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemManagementRepository extends JpaRepository<ItemManagementBean,String> {
    // 基本查詢方法
    List<ItemManagementBean> findByActiveTrue();

    // 查找最後一個商品ID用於生成序號
    Optional<ItemManagementBean> findFirstByOrderByItemIdDesc();

    // 按日期排序查詢所有商品
    List<ItemManagementBean> findAllByOrderByStartDateAsc();

    // 檢查商品是否存在於兌換列表中
    boolean existsByProductId(String productId);

    // 根據商品ID查詢商品
    @Query("SELECT p FROM ProductBean p WHERE p.productId = :productId")
    ProductBean findProductById(@Param("productId") String productId);

    // 根據商品類別查詢
    @Query("SELECT i FROM ItemManagementBean i WHERE i.product.productCategory = :category")
    List<ItemManagementBean> findByCategory(@Param("category") String category);

    // 查詢有效期內的商品
    @Query("SELECT i FROM ItemManagementBean i WHERE i.startDate <= :currentDate AND i.endDate >= :currentDate")
    List<ItemManagementBean> findValidItems(@Param("currentDate") LocalDate currentDate);

    // 查詢庫存不足的商品
    @Query("SELECT i FROM ItemManagementBean i WHERE i.itemMaximum <= :safeStock")
    List<ItemManagementBean> findLowStockItems(@Param("safeStock") int safeStock);

    // 更新可兌換數量
    @Transactional
    @Modifying
    @Query("UPDATE ItemManagementBean i SET i.itemMaximum = :newMaximum WHERE i.itemId = :itemId")
    void updateItemMaximum(@Param("itemId") String itemId, @Param("newMaximum") int newMaximum);

    // 檢查商品是否已在特定日期範圍內存在
    @Query("SELECT COUNT(i) > 0 FROM ItemManagementBean i WHERE i.productId = :productId " +
            "AND ((i.startDate BETWEEN :startDate AND :endDate) OR (i.endDate BETWEEN :startDate AND :endDate))")
    boolean existsByProductIdAndDateRange(
            @Param("productId") String productId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // 在ItemManagementRepository中添加回這個重要方法
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
}