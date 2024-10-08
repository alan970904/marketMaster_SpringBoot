package marketMaster.service.bonus;

import marketMaster.DTO.product.ProductCategoryDTO;
import marketMaster.DTO.product.ProductIdDTO;
import marketMaster.DTO.product.ProductNameDTO;

import marketMaster.bean.product.ProductBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BonusProductRepository extends JpaRepository<ProductBean,String> {

    @Query("SELECT DISTINCT new marketMaster.DTO.product.ProductCategoryDTO(p.productCategory) FROM ProductBean p")
    List<ProductCategoryDTO> findAllCategories();

    @Query("SELECT DISTINCT new marketMaster.DTO.product.ProductNameDTO(p.productName) FROM ProductBean p WHERE p.productCategory = :productCategory")
    List<ProductNameDTO> findAllProductNamesByCategory(@Param("productCategory") String productCategory);

    @Query("SELECT DISTINCT new marketMaster.DTO.product.ProductIdDTO(p.productId) FROM ProductBean p WHERE p.productName = :productName")
    List<ProductIdDTO> findAllProductIdByProductName(@Param("productName") String productName);

    @Query("SELECT p FROM ProductBean p WHERE p.productPrice <= :customerPoints AND p.numberOfInventory > 0")
    List<ProductBean> findExchangeableProducts(@Param("customerPoints") int customerPoints);

    @Modifying
    @Query("UPDATE ProductBean p SET p.numberOfInventory = :newInventory WHERE p.productId = :productId")
    void updateProductInventory(@Param("productId") String productId, @Param("newInventory") int newInventory);

    List<ProductBean> findByProductCategory(String category);
}