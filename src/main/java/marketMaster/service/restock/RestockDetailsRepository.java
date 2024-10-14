package marketMaster.service.restock;

import marketMaster.DTO.restock.restock.RestockDetailDTO;
import marketMaster.bean.restock.RestockDetailsBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RestockDetailsRepository extends JpaRepository<RestockDetailsBean, String> {

    Optional<RestockDetailsBean> findTopByOrderByDetailIdDesc();

    // 根據 restock 的 restockId 查詢 RestockDetailsBean
    @Query("SELECT new marketMaster.DTO.restock.restock.RestockDetailDTO( " +
            "r.restock.restockId, " +
            "r.detailId, " +
            "r.supplier.supplierId, " +
            "r.supplier.supplierName, " +
            "r.supplierProduct.product.productId, " +
            "r.supplierProduct.product.productName, " +
            "r.numberOfRestock, " +
            "r.priceAtRestock, " +
            "r.supplierProduct.productPrice, " +
            "r.restockTotalPrice ) " +
            "FROM RestockDetailsBean r " +
            "WHERE r.restock.restockId = :restockId")
    List<RestockDetailDTO> findRestockDetailByRestockId(@Param("restockId") String restockId);





    //跟新進貨明細表 商品數量價格 商品進貨價格 跟總金額
    @Transactional
    @Modifying
    @Query("UPDATE RestockDetailsBean rd SET rd.numberOfRestock = :numberOfRestock, " +
            "rd.priceAtRestock=:priceAtRestock, rd.restockTotalPrice = :restockTotalPrice WHERE rd.detailId = :detailId")
    void updateRestockNumberAndPrice(@Param("numberOfRestock") int numberOfRestock,
                                     @Param("restockTotalPrice") int restockTotalPrice,
                                     @Param("priceAtRestock") int priceAtRestock,
                                     @Param("detailId") String detailId);
    //查找所有屬於特定進貨編號的明細
    List<RestockDetailsBean> findByRestock_RestockId(String restockId);

    //通過detailId 找到supplierId
    @Query("SELECT r.supplier.supplierId FROM RestockDetailsBean r where r.detailId=:detailId")
    String findSupplierIdByDetailId(@Param("detailId")String detailId);

    //通過supplierId 查找所有restockTotalPrice
    @Query("SELECT r FROM RestockDetailsBean r where r.supplier.supplierId =:supplierId")
    List<RestockDetailsBean> findByRestock_SupplierId(@Param("supplierId") String supplierId);

    //根據restockId 找到所有關聯的detailId
    @Query("select r.detailId FROM RestockDetailsBean r where r.restock.restockId=:restockId")
    List<String> findDetailIdByRestockId(String restockId);

    //根據detail找到所有supplierId跟restockTotalPrice
    @Query("SELECT r.supplier.supplierId, r.restockTotalPrice FROM RestockDetailsBean r WHERE r.detailId IN :detailIds")
    List<Object[]> findSupplierIdAndTotalPriceByDetailIds(@Param("detailIds") List<String> detailIds);
    //根據supplierId 查找所有關聯的進貨明細
    @Query("SELECT r FROM RestockDetailsBean r WHERE r.supplier.supplierId = :supplierId")
    List<RestockDetailsBean> findBySupplierId(@Param("supplierId") String supplierId);

}


