package marketMaster.service.product;


import marketMaster.DTO.product.ProductCategoryDTO;
import marketMaster.DTO.product.ProductIdDTO;
import marketMaster.DTO.product.ProductNameDTO;
import marketMaster.DTO.product.ProductSupplierDTO;
import marketMaster.bean.product.ProductBean;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import marketMaster.bean.restock.RestockDetailsBean;
import marketMaster.bean.restock.SupplierProductsBean;


@Repository
public interface ProductRepository extends JpaRepository<ProductBean,String> {

    @Query("SELECT DISTINCT new marketMaster.DTO.product.ProductCategoryDTO(p.productCategory)FROM ProductBean p ")
    List<ProductCategoryDTO>findAllCategories();

    @Query("SELECT DISTINCT new marketMaster.DTO.product.ProductNameDTO(p.productName) FROM ProductBean p WHERE p.productCategory= :productCategory")
    List<ProductNameDTO>findAllProductNamesByCategory(@Param("productCategory") String productCategory);

    @Query("select DISTINCT new marketMaster.DTO.product.ProductIdDTO(p.productId)FROM ProductBean p WHERE p.productName= :productName")
    List<ProductIdDTO>findAllProductIdByProductName(@Param("productName")String productName);
    
    //JPA 自帶的模糊查詢
    Page<ProductBean> findByProductNameContaining(@Param("productName")String productName ,Pageable pgb);
    
    Page<ProductBean>  findByProductAvailable(boolean productAvailable ,Pageable pgb);
    
    
    @Query(value = "select * from products where Number_of_inventory <  product_safeinventory",nativeQuery = true)
    Page<ProductBean> findInventoryNotEnough(Pageable pgb);
    
    
//    @Query("SELECT rd.productionDate FROM RestockDetailsBean rd " +
//            "JOIN rd.supplierProduct sp " +
//            "JOIN sp.product p " +
//            "WHERE p.productId = :productId")
//    List<LocalDate> findProductionDatesByProductId(@Param("productId") String productId);
    
//    @Query("SELECT rd.productionDate FROM RestockDetailsBean rd WHERE rd.supplierProduct.product.productId = :productId")
//    List<LocalDate> findProductionDatesByProductId(@Param("productId") String productId);


    @Query("SELECT new marketMaster.DTO.product.ProductSupplierDTO(rd.supplierProduct.product.productId,rd.dueDate) FROM RestockDetailsBean rd WHERE rd.supplierProduct.supplierProductId = :supplierProductId")
    List<ProductSupplierDTO> findBySupplierId(@Param("supplierProductId") String supplierProductId);
    


}
