package marketMaster.service.bonus;

import marketMaster.bean.bonus.ItemManagementBean;
import marketMaster.DTO.bonus.ItemMgnDTO;
//import marketMaster.service.checkout.CheckoutService;
import marketMaster.service.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
@Transactional
public class ItemManagementService {
    private static final Logger logger = Logger.getLogger(ItemManagementService.class.getName());

    // 修正: 改用建構子注入
    private final ItemManagementRepository itemMgnRepo;
    @SuppressWarnings("unused")
    private final BonusExchangeRepository bonusExchangeRepo;
    @SuppressWarnings("unused")
    private final ProductRepository productRepo;

    @Autowired
    public ItemManagementService(ItemManagementRepository itemMgnRepo,
                                 BonusExchangeRepository bonusExchangeRepo,
                                 ProductRepository productRepo) {
        this.itemMgnRepo = itemMgnRepo;
        this.bonusExchangeRepo = bonusExchangeRepo;
        this.productRepo = productRepo;
    }
    // 轉換方法 - Stream改用傳統for循環和集合方式
    private ItemMgnDTO convertToDTO(ItemManagementBean item) {
        if (item == null) {
            return null;
        }
        try {
            ItemMgnDTO dto = new ItemMgnDTO();
            dto.setItemId(item.getItemId());
            if (item.getProduct() != null) {
                dto.setProductName(item.getProduct().getProductName());
                dto.setProductCategory(item.getProduct().getProductCategory());
                dto.setProductPhoto(item.getProduct().getProductPhoto());
            } else {
                dto.setProductName("Unknown");
                dto.setProductCategory("Unknown");
                dto.setProductPhoto(null);
            }
            dto.setItemPoints(item.getItemPoints());
            dto.setItemMaximum(item.getItemMaximum());
            dto.setStartDate(item.getStartDate());
            dto.setEndDate(item.getEndDate());
//            dto.setActive(item.isActive());  // 修正: 使用 isActive 而不是 setIsActive
            dto.setActive(item.isActive()); // 改為 dto.setActive(item.getActive());
            logger.info("轉換商品成功: " + item.getItemId());
            return dto;
        } catch (Exception e) {
            logger.severe("轉換商品失敗: " + e.getMessage());
            return null;
        }
    }

    //查詢所有可兌換商品並包含圖片數據
    public List<ItemMgnDTO> findAllItemsWithPhotos() {
        try {
            List<ItemManagementBean> itemList = itemMgnRepo.findAll();
            List<ItemMgnDTO> dtoList = new ArrayList<>();

            for (ItemManagementBean item : itemList) {
                ItemMgnDTO dto = convertToDTO(item);
                if(dto != null) {
                    dtoList.add(dto);
                }
            }
            logger.info("成功獲取 " + dtoList.size() + " 條商品記錄");
            return dtoList;
        } catch (Exception e) {
            logger.severe("獲取商品列表失敗: " + e.getMessage());
            throw new RuntimeException("獲取商品列表失敗", e);
        }
    }

    // 根據 ID 查詢單個商品
    public ItemManagementBean findById(String itemId) {
        try {
            return itemMgnRepo.findById(itemId).orElse(null);
        } catch (Exception e) {
            logger.severe("查詢商品失敗: " + e.getMessage());
            return null;
        }
    }

    //新增可兌換商品
    // 修正: addItem 返回型別BEAN改為DTO
    public ItemMgnDTO addItem(ItemManagementBean item) {
        try {
            validateItem(item);// 驗證
            if (item.getStartDate() == null) {
                item.setStartDate(LocalDate.now());
            }
            if (item.getEndDate() == null) {
                item.setEndDate(LocalDate.now().plusMonths(3)); // 預設3個月有效期
            }
            ItemManagementBean savedItem = itemMgnRepo.save(item);
            logger.info("成功新增商品: " + savedItem.getItemId());
            return convertToDTO(savedItem);
        } catch (Exception e) {
            logger.severe("新增商品失敗: " + e.getMessage());
            throw new RuntimeException("新增商品失敗", e);
        }
    }
    // 修正: updateItem 方法添加 itemId 參數   返回型別BEAN改為DTO
    @Transactional
    public ItemMgnDTO updateItem(String itemId, ItemManagementBean item) {
        try {
            // 檢查商品是否存在
            if (!itemMgnRepo.existsById(itemId)) {
                throw new RuntimeException("商品不存在: " + itemId);
            }
            // 驗證
            validateItem(item);
            item.setItemId(itemId);  // 確保 ID 正確
            ItemManagementBean updatedItem = itemMgnRepo.save(item);
            logger.info("成功更新商品: " + itemId);
            return convertToDTO(updatedItem);
        } catch (Exception e) {
            logger.severe("更新商品失敗: " + e.getMessage());
            throw new RuntimeException("更新商品失敗", e);
        }
    }

    public void deleteItem(String itemId) {
        try {
            if (!itemMgnRepo.existsById(itemId)) {
                throw new RuntimeException("商品不存在: " + itemId);
            }
            itemMgnRepo.deleteById(itemId);
            logger.info("成功刪除商品: " + itemId);
        } catch (Exception e) {
            logger.severe("刪除商品失敗: " + e.getMessage());
            throw new RuntimeException("刪除商品失敗", e);
        }
    }
    // 新增切換狀態方法
    @Transactional
    public boolean toggleStatus(String itemId) {
        try {
            ItemManagementBean item = itemMgnRepo.findById(itemId).orElse(null);
            if (item == null) {
                return false;
            }

            item.setActive(!item.isActive());
//            item.setActive(!item.getActive());  // 使用 getActive() 而不是 isActive()
            itemMgnRepo.save(item);
            logger.info("商品狀態已更新: " + itemId);
            return true;
        } catch (Exception e) {
            logger.severe("更新商品狀態失敗: " + e.getMessage());
            return false;
        }
    }

    // 驗證方法
    private void validateItem(ItemManagementBean item) {
        if (item == null) {
            throw new IllegalArgumentException("商品不能為空");
        }
        if (item.getItemPoints() <= 0) {
            throw new IllegalArgumentException("兌換點數必須大於0");
        }
        if (item.getItemMaximum() <= 0) {
            throw new IllegalArgumentException("可兌換數量必須大於0");
        }
        if (item.getStartDate() != null && item.getEndDate() != null &&
                item.getStartDate().isAfter(item.getEndDate())) {
            throw new IllegalArgumentException("開始日期不能晚於結束日期");
        }
    }
}
