package marketMaster.service.restock;

import marketMaster.DTO.restock.restock.RestockDetailDTO;
import marketMaster.bean.restock.RestockDetailsBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface RestockDetailsRepository extends JpaRepository<RestockDetailsBean, String> {

    Optional<RestockDetailsBean> findTopByOrderByDetailIdDesc();

    // 根據 restock 的 restockId 查詢 RestockDetailsBean
    @Query("SELECT new marketMaster.DTO.restock.restock.RestockDetailDTO( r.restock.restockId,r.detailId,r.supplier.supplierId,r.supplier.supplierName,r.supplierProduct.product.productId,r.supplierProduct.product.productName,r.supplierProduct.productPrice,r.numberOfRestock,r.restockTotalPrice ) "+
            "FROM RestockDetailsBean r "+
            "WHERE r.restock.restockId= :restockId")
    List<RestockDetailDTO> findRestockDetailByRestockId(@Param("restockId") String restockId);


    //跟新進貨明細表 商品數量價格 跟總金額
    @Transactional
    @Modifying
    @Query("UPDATE RestockDetailsBean rd set rd.numberOfRestock=:numberOfRestock ,rd.supplierProduct.productPrice=:productPrice,rd.restockTotalPrice=:restockTotalPrice WHERE rd.detailId=:detailId")
    void updateRestockNumberAndPrice (@Param("numberOfRestock")int numberOfRestock,@Param("productPrice")int productPrice,@Param("restockTotalPrice")int restockTotalPrice,@Param("detailId")String detailId);

    //查找所有屬於特定進貨編號的明細
    List<RestockDetailsBean> findByRestock_RestockId(String restockId);

}


