package marketMaster.service.product;


import marketMaster.DTO.product.ProductCategoryDTO;
import marketMaster.DTO.product.ProductIdDTO;
import marketMaster.DTO.product.ProductNameDTO;

import marketMaster.bean.product.ProductBean;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    
//    @Query("from ProductBean where productName LIKE :productName")
//    Page<ProductBean> findProductByProductNameLike(@Param("productName")String productName ,Pageable pgb);

}
