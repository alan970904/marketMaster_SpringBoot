package marketMaster.service.bonus;

import marketMaster.bean.product.ProductBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductExchangeManagementRepository extends JpaRepository<ProductBean, String> {
    // 查詢所有可用商品
    @Query("FROM ProductBean WHERE productAvailable = true")
    List<ProductBean> findAllAvailableProducts();

    // 根據商品類別查詢可用商品
    @Query("FROM ProductBean WHERE productCategory = :category AND productAvailable = true")
    List<ProductBean> findAvailableByCategory(@Param("category") String category);

    // 查詢所有可用商品類別
    @Query("SELECT DISTINCT p.productCategory FROM ProductBean p WHERE p.productAvailable = true")
    List<String> findAllAvailableCategories();
}