package marketMaster.service.bonus;

import marketMaster.bean.bonus.ItemManagementBean;
import marketMaster.DTO.bonus.ItemMgnDTO;
import marketMaster.bean.product.ProductBean;
import marketMaster.exception.BonusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class ItemManagementService {
    private static final Logger logger = Logger.getLogger(ItemManagementService.class.getName());

    private final ItemManagementRepository itemMgnRepo;
    private final BonusExchangeRepository bonusExchangeRepo;
    private final ProductExchangeManagementRepository productExMgnRepo;

    @Autowired
    public ItemManagementService(ItemManagementRepository itemMgnRepo,
                                 BonusExchangeRepository bonusExchangeRepo,
                                 ProductExchangeManagementRepository productExMgnRepo) {
        this.itemMgnRepo = itemMgnRepo;
        this.bonusExchangeRepo = bonusExchangeRepo;
        this.productExMgnRepo = productExMgnRepo;
    }

    // DTO轉換方法
    private ItemMgnDTO convertToDTO(ItemManagementBean item) {
        if (item == null) {
            return null;
        }
        try {
            ItemMgnDTO dto = new ItemMgnDTO();
            dto.setItemId(item.getItemId());
            dto.setProductId(item.getProductId());

            ProductBean product = item.getProduct();
            if (product != null) {
                dto.setProductName(product.getProductName());
                dto.setProductCategory(product.getProductCategory());
                dto.setProductPhoto(product.getProductPhoto());
                dto.setProductPrice(product.getProductPrice());
            } else {
                dto.setProductName("未知商品");
                dto.setProductCategory("未知類別");
                dto.setProductPhoto(null);
                dto.setProductPrice(0);
            }

            dto.setItemPoints(item.getItemPoints());
            dto.setItemMaximum(item.getItemMaximum());
            dto.setStartDate(item.getStartDate());
            dto.setEndDate(item.getEndDate());
            dto.setActive(item.isActive());
            logger.info("成功轉換商品: " + item.getItemId());
            return dto;
        } catch (Exception e) {
            logger.severe("轉換商品失敗: " + e.getMessage());
            return null;
        }
    }

    // 查詢所有商品並按日期排序
    public List<ItemMgnDTO> findAllItemsWithPhotos() {
        try {
            List<ItemManagementBean> itemList = itemMgnRepo.findAllByOrderByStartDateAsc();
            List<ItemMgnDTO> dtoList = new ArrayList<>();

            for (ItemManagementBean item : itemList) {
                ItemMgnDTO dto = convertToDTO(item);
                if (dto != null) {
                    dtoList.add(dto);
                }
            }

            logger.info("成功獲取 " + dtoList.size() + " 條商品記錄");
            return dtoList;
        } catch (Exception e) {
            logger.severe("獲取商品列表失敗: " + e.getMessage());
            throw new BonusException.ItemManagementException("獲取商品列表失敗", e);
        }
    }

    // 查詢單個商品
    public ItemManagementBean findById(String itemId) {
        try {
            ItemManagementBean item = itemMgnRepo.findById(itemId).orElse(null);
            if (item == null) {
                throw new BonusException.InvalidItemDataException("商品不存在: " + itemId);
            }
            return item;
        } catch (BonusException.InvalidItemDataException e) {
            throw e;
        } catch (Exception e) {
            logger.severe("查詢商品失敗: " + e.getMessage());
            throw new BonusException.ItemManagementException("查詢商品失敗", e);
        }
    }

    // 生成新的商品ID
    public String generateIncrementalId() {
        try {
            ItemManagementBean lastItem = itemMgnRepo.findFirstByOrderByItemIdDesc()
                    .orElse(null);

            if (lastItem == null) {
                return "IM001";
            }

            String lastId = lastItem.getItemId();
            Pattern pattern = Pattern.compile("IM(\\d{3})");
            Matcher matcher = pattern.matcher(lastId);

            if (!matcher.find()) {
                throw new BonusException.IdGenerationException("無效的ID格式: " + lastId);
            }

            int currentNumber = Integer.parseInt(matcher.group(1));
            int nextNumber = currentNumber + 1;

            if (nextNumber > 999) {
                throw new BonusException.IdGenerationException("ID序列已達到最大值");
            }

            return String.format("EX%05d", nextNumber);
        } catch (Exception e) {
            logger.severe("生成商品ID失敗: " + e.getMessage());
            throw new BonusException.IdGenerationException("生成商品ID失敗", e);
        }
    }

    // 新增商品
    public ItemMgnDTO addItem(ItemManagementBean item) {
        try {
            // 自動生成ID
            item.setItemId(generateIncrementalId());

            // 設置日期
            LocalDate today = LocalDate.now();
            if (item.getStartDate() == null) {
                item.setStartDate(today);
            }
            if (item.getEndDate() == null) {
                item.setEndDate(item.getStartDate().plusMonths(2));
            }

            // 驗證商品是否重複
            if (itemMgnRepo.existsByProductId(item.getProductId())) {
                throw new BonusException.DuplicateItemException("此商品已在兌換列表中");
            }

            // 檢查日期範圍內是否有相同商品
            if (itemMgnRepo.existsByProductIdAndDateRange(
                    item.getProductId(),
                    item.getStartDate(),
                    item.getEndDate())) {
                throw new BonusException.DuplicateItemException("選擇的日期範圍內已存在此商品");
            }

            // 檢查和設置商品點數
            ProductBean product = itemMgnRepo.findProductById(item.getProductId());
            if (product == null) {
                throw new BonusException.InvalidItemDataException("商品不存在");
            }

            // 確保兌換點數符合商品價格
            if (item.getItemPoints() < product.getProductPrice()) {
                item.setItemPoints(product.getProductPrice());
            }

            // 設置為啟用狀態並驗證
            item.setActive(true);
            validateItem(item);

            // 保存商品
            ItemManagementBean savedItem = itemMgnRepo.save(item);
            logger.info("成功新增商品: " + savedItem.getItemId());
            return convertToDTO(savedItem);

        } catch (BonusException.ItemManagementException e) {
            throw e;
        } catch (Exception e) {
            logger.severe("新增商品失敗: " + e.getMessage());
            throw new BonusException.ItemManagementException("新增商品失敗", e);
        }
    }

    // 更新商品
    @Transactional
    public ItemMgnDTO updateItem(String itemId, ItemManagementBean item) {
        try {
            ItemManagementBean existingItem = itemMgnRepo.findById(itemId)
                    .orElseThrow(() -> new BonusException.InvalidItemDataException("商品不存在: " + itemId));

            // 檢查除了自己以外是否有重複商品
            if (!itemId.equals(item.getItemId()) &&
                    itemMgnRepo.existsByProductIdAndDateRange(
                            item.getProductId(),
                            item.getStartDate(),
                            item.getEndDate())) {
                throw new BonusException.DuplicateItemException("選擇的日期範圍內已存在此商品");
            }

            // 設置結束日期
            if (!existingItem.getStartDate().equals(item.getStartDate())) {
                item.setEndDate(item.getStartDate().plusMonths(2));
            }

            // 檢查商品點數
            ProductBean product = itemMgnRepo.findProductById(item.getProductId());
            if (product == null) {
                throw new BonusException.InvalidItemDataException("商品不存在");
            }

            // 確保兌換點數合理
            if (item.getItemPoints() < product.getProductPrice()) {
                item.setItemPoints(product.getProductPrice());
            }

            // 保持原有ID和驗證
            item.setItemId(itemId);
            validateItem(item);

            // 保存更新
            ItemManagementBean updatedItem = itemMgnRepo.save(item);
            logger.info("成功更新商品: " + itemId);
            return convertToDTO(updatedItem);

        } catch (BonusException.ItemManagementException e) {
            throw e;
        } catch (Exception e) {
            logger.severe("更新商品失敗: " + e.getMessage());
            throw new BonusException.ItemManagementException("更新商品失敗", e);
        }
    }

    // 刪除商品
    public void deleteItem(String itemId) {
        try {
            if (!itemMgnRepo.existsById(itemId)) {
                throw new BonusException.InvalidItemDataException("商品不存在: " + itemId);
            }

            // 檢查是否有相關的兌換記錄
            if (bonusExchangeRepo.existsByExchangeItemId(itemId)) {
                throw new BonusException.InvalidItemDataException("此商品已有兌換記錄，無法刪除");
            }

            itemMgnRepo.deleteById(itemId);
            logger.info("成功刪除商品: " + itemId);
        } catch (BonusException.ItemManagementException e) {
            throw e;
        } catch (Exception e) {
            logger.severe("刪除商品失敗: " + e.getMessage());
            throw new BonusException.ItemManagementException("刪除商品失敗", e);
        }
    }

    // 切換商品狀態
    @Transactional
    public boolean toggleStatus(String itemId) {
        try {
            ItemManagementBean item = itemMgnRepo.findById(itemId)
                    .orElseThrow(() -> new BonusException.InvalidItemDataException("商品不存在: " + itemId));

            // 檢查日期有效性
            LocalDate today = LocalDate.now();
            if (!item.isActive() && (today.isAfter(item.getEndDate()) || today.isBefore(item.getStartDate()))) {
                throw new BonusException.InvalidItemDataException("商品不在有效期內，無法啟用");
            }

            item.setActive(!item.isActive());
            itemMgnRepo.save(item);
            logger.info("商品狀態已更新: " + itemId + ", 新狀態: " + item.isActive());
            return true;

        } catch (Exception e) {
            logger.severe("更新商品狀態失敗: " + e.getMessage());
            return false;
        }
    }

    // 驗證商品數據
    private void validateItem(ItemManagementBean item) {
        if (item == null) {
            throw new BonusException.InvalidItemDataException("商品不能為空");
        }

        if (item.getProductId() == null || item.getProductId().trim().isEmpty()) {
            throw new BonusException.InvalidItemDataException("必須選擇商品");
        }

        if (item.getItemPoints() <= 0) {
            throw new BonusException.InvalidItemDataException("兌換點數必須大於0");
        }

        if (item.getItemMaximum() <= 0) {
            throw new BonusException.InvalidItemDataException("可兌換數量必須大於0");
        }

        // 日期驗證
        LocalDate today = LocalDate.now();
        if (item.getStartDate() == null) {
            throw new BonusException.InvalidItemDataException("必須設置開始日期");
        }

        if (item.getEndDate() == null) {
            throw new BonusException.InvalidItemDataException("必須設置結束日期");
        }

        if (item.getStartDate().isAfter(item.getEndDate())) {
            throw new BonusException.InvalidItemDataException("開始日期不能晚於結束日期");
        }

        if (item.getStartDate().isBefore(today)) {
            throw new BonusException.InvalidItemDataException("開始日期不能早於今天");
        }

        if (item.getEndDate().isBefore(item.getStartDate().plusDays(1))) {
            throw new BonusException.InvalidItemDataException("活動時間至少要一天");
        }
    }

    // 取得可用商品列表
    public List<ProductBean> getAvailableProducts() {
        try {
            List<ProductBean> products = productExMgnRepo.findAllAvailableProducts();
            logger.info("成功獲取 " + products.size() + " 個可用商品");
            return products;
        } catch (Exception e) {
            logger.severe("獲取可用商品失敗: " + e.getMessage());
            throw new BonusException.ItemManagementException("獲取可用商品失敗", e);
        }
    }

    // 根據類別取得可用商品
    public List<ProductBean> getAvailableProductsByCategory(String category) {
        try {
            List<ProductBean> products = productExMgnRepo.findAvailableByCategory(category);
            logger.info("成功獲取類別 " + category + " 的 " + products.size() + " 個可用商品");
            return products;
        } catch (Exception e) {
            logger.severe("獲取類別商品失敗: " + e.getMessage());
            throw new BonusException.ItemManagementException("獲取類別商品失敗", e);
        }
    }

    // 取得所有可用類別
    public List<String> getAllAvailableCategories() {
        try {
            List<String> categories = productExMgnRepo.findAllAvailableCategories();
            logger.info("成功獲取 " + categories.size() + " 個商品類別");
            return categories;
        } catch (Exception e) {
            logger.severe("獲取商品類別失敗: " + e.getMessage());
            throw new BonusException.ItemManagementException("獲取商品類別失敗", e);
        }
    }

    // 檢查商品是否在列表中
    public boolean checkProductExists(String productId) {
        try {
            return itemMgnRepo.existsByProductId(productId);
        } catch (Exception e) {
            logger.severe("檢查商品存在失敗: " + e.getMessage());
            throw new BonusException.ItemManagementException("檢查商品存在失敗", e);
        }
    }

    // 檢查商品在特定日期範圍內是否存在
    public boolean checkProductExistsInDateRange(String productId, LocalDate startDate, LocalDate endDate) {
        try {
            return itemMgnRepo.existsByProductIdAndDateRange(productId, startDate, endDate);
        } catch (Exception e) {
            logger.severe("檢查商品日期範圍失敗: " + e.getMessage());
            throw new BonusException.ItemManagementException("檢查商品日期範圍失敗", e);
        }
    }
}