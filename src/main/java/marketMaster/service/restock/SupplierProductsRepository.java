package marketMaster.service.restock;

import marketMaster.DTO.restock.SupplierDTO.SupplierProductDTO;
import marketMaster.DTO.restock.SupplierDTO.SupplierProductDetailDTO;
import marketMaster.bean.restock.SupplierProductsBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SupplierProductsRepository extends JpaRepository<SupplierProductsBean,String> {
    //    透過供應商id找尋該id所有的商品
    @Query("SELECT new marketMaster.DTO.restock.SupplierDTO.SupplierProductDTO(s.product.productId, s.product.productName) " +
            "FROM SupplierProductsBean s " +
            "WHERE s.supplier.supplierId = :supplierId")
    List<SupplierProductDTO> findProductsBySupplierId(@Param("supplierId") String supplierId);

    //    頁碼透過供應商id找尋該id所有的商品
    @Query("SELECT new marketMaster.DTO.restock.SupplierDTO.SupplierProductDetailDTO(s.supplierProductId,s.product.productId,s.product.productName,s.productPrice,s.status)FROM SupplierProductsBean s WHERE s.supplier.supplierId = :supplierId")
    Page<SupplierProductDetailDTO> findProductsBySupplierIdPage(@Param("supplierId") String supplierId, Pageable pageable);

    @Query("SELECT new marketMaster.DTO.restock.SupplierDTO.SupplierProductDetailDTO(s.supplierProductId,s.product.productId,s.product.productName,s.productPrice,s.status)FROM SupplierProductsBean s WHERE s.supplier.supplierId = :supplierId")
    List<SupplierProductDetailDTO> findAllBySupplierId(@Param("supplierId") String supplierId);

    // 檢查供應商商品是否存在
    boolean existsBySupplier_SupplierIdAndProduct_ProductId(String supplierId, String productId);

    // 根據商品查詢有哪些供應商
    @Query("SELECT sp FROM SupplierProductsBean sp JOIN sp.supplier s WHERE sp.product.productId = :productId")
    List<SupplierProductsBean> findProductAllSupplier(@Param("productId") String productId);

    //查詢最新的supplier_product_id
    //mysql
//    @Query(value = "SELECT supplier_product_id FROM supplier_products ORDER BY supplier_product_id DESC LIMIT 1", nativeQuery = true)
//    String findLatestSupplierProductId();
    //sqlserver
    @Query(value = "SELECT TOP 1 supplier_product_id FROM supplier_products ORDER BY supplier_product_id DESC", nativeQuery = true)
    String findLatestSupplierProductId();
    //透過productＩd查詢商品價格
    @Query("SELECT s.productPrice FROM SupplierProductsBean s WHERE s.product.productId = :productId")
    Integer findProductPriceByProductId(@Param("productId") String productId);


    Optional<SupplierProductsBean> findBySupplier_SupplierIdAndProduct_ProductId(String supplierId, String productId);

    // 新增的方法：根据 supplierId 和 productId 查询 productPrice
    @Query("SELECT sp.productPrice FROM SupplierProductsBean sp WHERE sp.supplier.supplierId = :supplierId AND sp.product.productId = :productId")
    Optional<Integer> findProductPriceBySupplierIdAndProductId(@Param("supplierId") String supplierId, @Param("productId") String productId);


}
