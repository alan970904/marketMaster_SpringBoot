package marketMaster.service.restock;

import marketMaster.DTO.restock.restock.RestockDetailDTO;
import marketMaster.bean.restock.RestockDetailsBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestockDetailsRepository extends JpaRepository<RestockDetailsBean, String> {

    Optional<RestockDetailsBean> findTopByOrderByDetailIdDesc();

    // 根據 restock 的 restockId 查詢 RestockDetailsBean
    @Query("SELECT new marketMaster.DTO.restock.restock.RestockDetailDTO( r.restock.restockId,r.detailId,r.supplier.supplierId,r.supplier.supplierName,r.supplierProduct.product.productId,r.supplierProduct.product.productName,r.supplierProduct.productPrice,r.numberOfRestock,r.restockTotalPrice ) "+
            "FROM RestockDetailsBean r "+
            "WHERE r.restock.restockId= :restockId")
    List<RestockDetailDTO> findRestockDetailByRestockId(@Param("restockId") String restockId);
}


