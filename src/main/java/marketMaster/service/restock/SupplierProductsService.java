package marketMaster.service.restock;

import marketMaster.DTO.restock.SupplierDTO.SupplierProductDTO;
import marketMaster.DTO.restock.SupplierDTO.SupplierProductDetailDTO;
import marketMaster.bean.restock.SupplierProductsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierProductsService {
    @Autowired
    private SupplierProductsRepository supplierProductsRepository;
    @Transactional
    //獲取所有供應商可提供商品
    public List<SupplierProductDetailDTO>getAllSupplierProductBySupplierId(String supplierId) {
        return supplierProductsRepository.findAllBySupplierId(supplierId);
    }
//    透過供應商id找尋該id所有的商品
    public List<SupplierProductDTO>findProductsBySupplierId(String supplierId) {
     return supplierProductsRepository.findProductsBySupplierId(supplierId);
    }

    //    頁數透過供應商id找尋該id所有的商品
    public Page<SupplierProductDetailDTO> findProductsBySupplierIdPage(String supplierId, Pageable pageable) {
        return supplierProductsRepository.findProductsBySupplierIdPage(supplierId,pageable);
    }

    // 新增供應商提供的商品
    public SupplierProductsBean addSupplierProduct(SupplierProductsBean supplierProductsBean) {
        String supplierProductId =generateNewSupplierProductId();
        String supplierId = supplierProductsBean.getSupplier().getSupplierId();
        String productId = supplierProductsBean.getProduct().getProductId();
        supplierProductsBean.setSupplierProductId(supplierProductId);
        // 檢查商品是否已存在
        if (supplierProductsRepository.existsBySupplier_SupplierIdAndProduct_ProductId(supplierId, productId)) {
            throw new RuntimeException("該供應商已經提供了此商品，無法重複添加。");
        }
        // 保存新商品
        return supplierProductsRepository.save(supplierProductsBean);
    }
    // 修改供應商進價 修改供應商供貨狀態 0=目前無法供貨 1=可供貨
    public void updateSupplierProduct(SupplierProductsBean supplierProductsBean) {
        String supplierProductId = supplierProductsBean.getSupplierProductId();
      Optional<SupplierProductsBean> productsBean= supplierProductsRepository.findById(supplierProductId);
      if (productsBean.isPresent()) {
          supplierProductsRepository.save(supplierProductsBean);
          System.out.println("更新成功");
      }

    }

    //   刪除供應商商品
    @Transactional
        public void deleteSupplierProduct(String supplierProductId) {
            supplierProductsRepository.deleteById(supplierProductId);
        }

    // 根據商品查詢有哪些供應商
    public List<SupplierProductsBean> findProductAllSupplier(String productId) {
        return supplierProductsRepository.findProductAllSupplier(productId);
    }

    // 生成新的 supplier_product_id
    private String generateNewSupplierProductId() {
        // 查詢當前最大的 supplier_product_id
        String latestId = supplierProductsRepository.findLatestSupplierProductId();

        // 初始化新的 ID
        String newId;

        if (latestId != null && latestId.startsWith("SP")) {
            // 提取數字部分，並將其轉換為整數
            int currentNumber = Integer.parseInt(latestId.substring(2));
            // 增加 1 並格式化為三位數
            newId = String.format("SP%03d", currentNumber + 1);
        } else {
            // 如果資料庫中沒有 ID，從 SP001 開始
            newId = "SP001";
        }

        return newId;
    }
    public Integer findProductPriceByProductId(String productId) {
    return supplierProductsRepository.findProductPriceByProductId(productId);
    }
    // 新增的方法：根据 supplierId 和 productId 查询 productPrice
    public Optional<Integer> getProductPrice(String supplierId, String productId) {
        return supplierProductsRepository.findProductPriceBySupplierIdAndProductId(supplierId, productId);
    }




}
