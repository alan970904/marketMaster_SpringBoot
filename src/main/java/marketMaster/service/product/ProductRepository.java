package marketMaster.service.product;

import marketMaster.bean.product.ProductBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProductRepository extends JpaRepository<ProductBean,String> {

    @Query("SELECT DISTINCT new marketMaster.DTO.product.ProductCategoryDTO(p.productCategory)FROM ProductBean p ")
    List<ProductBean>findAllCategories();

    @Query("SELECT DISTINCT new marketMaster.DTO.product.ProductNameDTO(p.productName) FROM ProductBean p WHERE p.productCategory= :productCategory")
    List<ProductBean>findAllProductNamesByCategory(@Param("productCategory") String productCategory);


}
