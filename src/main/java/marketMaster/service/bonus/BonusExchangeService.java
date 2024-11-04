package marketMaster.service.bonus;

import marketMaster.DTO.bonus.BonusExchangeDTO;
import marketMaster.DTO.bonus.CustomerPointsDTO;
import marketMaster.DTO.bonus.ItemMgnDTO;
import marketMaster.bean.bonus.ItemManagementBean;
import marketMaster.bean.bonus.PointsHistoryBean;
import marketMaster.bean.customer.CustomerBean;
import marketMaster.bean.bonus.BonusExchangeBean;
import marketMaster.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class BonusExchangeService {
    private static final Logger logger = Logger.getLogger(BonusExchangeService.class.getName());

    @Autowired
    private ItemManagementRepository itemMgnRepo;
    @Autowired
    private CustomerPointsRepository cusRepo;
    @Autowired
    private BonusExchangeRepository exRepo;
    @Autowired
    private PointsHistoryRepository pointsHistoryRepo;

    // 查詢可兌換商品
    @Transactional(readOnly = true)
    public List<ItemMgnDTO> getExchangeableProducts(String customerTel) throws DataAccessException {
        try {
            logger.info("開始查詢會員可兌換商品: " + customerTel);

            // 檢查會員是否存在
            Optional<CustomerBean> customerOpt = cusRepo.findById(customerTel);
            if (!customerOpt.isPresent()) {
                throw new DataAccessException("找不到此會員");
            }

            CustomerBean customer = customerOpt.get();
            int customerPoints = customer.getTotalPoints();
            LocalDate now = LocalDate.now();

            // 獲取可兌換商品列表
            List<ItemManagementBean> items = itemMgnRepo.findExchangeableItems(now, customerPoints);
            List<ItemMgnDTO> dtoList = new ArrayList<>();

            for (ItemManagementBean item : items) {
                if (item.getProduct() != null) {
                    ItemMgnDTO dto = convertToItemMgnDTO(item);
                    dtoList.add(dto);
                }
            }
            return dtoList;
        } catch (Exception e) {
            logger.severe("獲取可兌換商品失敗: " + e.getMessage());
            throw new DataAccessException("獲取可兌換商品失敗: " + e.getMessage());
        }
    }

    // 查詢會員點數
    @Transactional(readOnly = true)
    public CustomerPointsDTO getCustomerPoints(String customerTel) throws DataAccessException {
        try {
            Optional<CustomerBean> customerOpt = cusRepo.findById(customerTel);
            if (!customerOpt.isPresent()) {
                throw new DataAccessException("找不到此會員");
            }

            CustomerBean customer = customerOpt.get();
            return new CustomerPointsDTO(
                    customer.getCustomerTel(),
                    customer.getCustomerName(),
                    customer.getTotalPoints()
            );
        } catch (Exception e) {
            throw new DataAccessException("獲取會員點數失敗: " + e.getMessage());
        }
    }

    // 驗證商品
    @Transactional(readOnly = true)
    public ItemMgnDTO validateExchangeItem(String itemId) throws DataAccessException {
        try {
            Optional<ItemManagementBean> itemOpt = itemMgnRepo.findById(itemId);
            if (!itemOpt.isPresent()) {
                throw new DataAccessException("商品不存在");
            }

            ItemManagementBean item = itemOpt.get();
            LocalDate now = LocalDate.now();

            if (!item.isActive()) {
                throw new DataAccessException("商品已下架");
            }
            if (now.isBefore(item.getStartDate())) {
                throw new DataAccessException("商品尚未開始兌換");
            }
            if (now.isAfter(item.getEndDate())) {
                throw new DataAccessException("商品兌換期已結束");
            }
            if (item.getItemMaximum() <= 0) {
                throw new DataAccessException("商品庫存不足");
            }

            return convertToItemMgnDTO(item);
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    // 執行兌換
    @Transactional
    public void save(BonusExchangeBean exchange) throws DataAccessException {
        try {
            logger.info("開始執行兌換操作");

            // 驗證數據
            if (exchange.getNumberOfExchange() <= 0) {
                throw new DataAccessException("兌換數量必須大於0");
            }

            // 檢查會員
            Optional<CustomerBean> customerOpt = cusRepo.findById(exchange.getCustomerTel());
            if (!customerOpt.isPresent()) {
                throw new DataAccessException("找不到會員");
            }
            CustomerBean customer = customerOpt.get();

            // 檢查商品
            Optional<ItemManagementBean> itemOpt = itemMgnRepo.findById(exchange.getItemId());
            if (!itemOpt.isPresent()) {
                throw new DataAccessException("找不到商品");
            }
            ItemManagementBean item = itemOpt.get();

            // 驗證商品狀態
            validateExchangeItemStatus(item);

            // 驗證庫存
            if (item.getItemMaximum() < exchange.getNumberOfExchange()) {
                throw new DataAccessException("商品庫存不足");
            }

            // 計算所需點數
            int requiredPoints = item.getItemPoints() * exchange.getNumberOfExchange();
            if (customer.getTotalPoints() < requiredPoints) {
                throw new DataAccessException("會員點數不足");
            }

            // 更新會員點數
            customer.setTotalPoints(customer.getTotalPoints() - requiredPoints);
            cusRepo.save(customer);

            // 更新商品庫存
            item.setItemMaximum(item.getItemMaximum() - exchange.getNumberOfExchange());
            itemMgnRepo.save(item);

            // 生成兌換記錄
            String exchangeId = generateNextExchangeId();
            exchange.setExchangeId(exchangeId);
            exchange.setUsePoints(requiredPoints);
            exchange.setExchangeDate(LocalDate.now());
            exRepo.save(exchange);

            // 記錄點數變動
            savePointsHistory(exchange, "use");

            logger.info("兌換操作完成");

        } catch (Exception e) {
            logger.severe("執行兌換失敗: " + e.getMessage());
            throw new DataAccessException("執行兌換失敗: " + e.getMessage());
        }
    }

    // 查詢所有兌換記錄
    @Transactional(readOnly = true)
    public List<BonusExchangeBean> findAllBonusExchange() throws DataAccessException {
        try {
            return exRepo.findAll();
        } catch (Exception e) {
            throw new DataAccessException("獲取所有兌換記錄失敗: " + e.getMessage());
        }
    }

    // 獲取兌換記錄
    @Transactional(readOnly = true)
    public List<BonusExchangeDTO> getExchangeRecordsByCustomer(String customerTel) throws DataAccessException {
        try {
            List<BonusExchangeBean> records = exRepo.findByCustomerTel(customerTel);
            List<BonusExchangeDTO> dtoList = new ArrayList<>();

            for (BonusExchangeBean record : records) {
                BonusExchangeDTO dto = convertToExchangeDTO(record);
                dtoList.add(dto);
            }

            return dtoList;
        } catch (Exception e) {
            throw new DataAccessException("獲取兌換記錄失敗: " + e.getMessage());
        }
    }

    // 更新兌換記錄
    @Transactional
    public void updateBonusExchange(BonusExchangeBean updatedExchange) throws DataAccessException {
        try {
            logger.info("開始更新兌換記錄: " + updatedExchange.getExchangeId());

            // 1. 獲取並驗證原有記錄
            Optional<BonusExchangeBean> existingExchangeOpt =
                    exRepo.findById(updatedExchange.getExchangeId());
            if (!existingExchangeOpt.isPresent()) {
                throw new DataAccessException("找不到原兌換記錄");
            }
            BonusExchangeBean existingExchange = existingExchangeOpt.get();

            // 2. 驗證會員和商品
            Optional<CustomerBean> customerOpt = cusRepo.findById(updatedExchange.getCustomerTel());
            if (!customerOpt.isPresent()) {
                throw new DataAccessException("找不到會員");
            }
            CustomerBean customer = customerOpt.get();

            Optional<ItemManagementBean> itemOpt = itemMgnRepo.findById(updatedExchange.getItemId());
            if (!itemOpt.isPresent()) {
                throw new DataAccessException("找不到商品");
            }
            ItemManagementBean item = itemOpt.get();

            // 3. 驗證商品狀態
            validateExchangeItemStatus(item);

            // 4. 計算點數和數量差異
            int oldPoints = existingExchange.getUsePoints();
            int newPoints = item.getItemPoints() * updatedExchange.getNumberOfExchange();
            int pointsDiff = newPoints - oldPoints;

            int oldQuantity = existingExchange.getNumberOfExchange();
            int newQuantity = updatedExchange.getNumberOfExchange();
            int quantityDiff = newQuantity - oldQuantity;

            // 5. 驗證更新條件
            if (newQuantity <= 0) {
                throw new DataAccessException("兌換數量必須大於0");
            }

            if (pointsDiff > 0) {
                if (customer.getTotalPoints() < pointsDiff) {
                    throw new DataAccessException("會員點數不足");
                }
            }

            if (quantityDiff > 0) {
                if (item.getItemMaximum() < quantityDiff) {
                    throw new DataAccessException("商品庫存不足");
                }
            }

            // 6. 執行更新
            // 更新會員點數
            customer.setTotalPoints(customer.getTotalPoints() - pointsDiff);
            cusRepo.save(customer);

            // 更新商品庫存
            item.setItemMaximum(item.getItemMaximum() - quantityDiff);
            itemMgnRepo.save(item);

            // 更新兌換記錄
            existingExchange.setNumberOfExchange(newQuantity);
            existingExchange.setUsePoints(newPoints);
            existingExchange.setExchangeDate(LocalDate.now());
            exRepo.save(existingExchange);

            // 記錄點數變動
            if (pointsDiff != 0) {
                savePointsHistory(existingExchange, pointsDiff > 0 ? "use" : "refund");
            }

            logger.info("兌換記錄更新成功");
        } catch (Exception e) {
            logger.severe("更新兌換記錄失敗: " + e.getMessage());
            throw new DataAccessException(e.getMessage());
        }
    }

    // 刪除兌換記錄
    @Transactional
    public void deleteById(String exchangeId) throws DataAccessException {
        try {
            Optional<BonusExchangeBean> exchangeOpt = exRepo.findById(exchangeId);
            if (!exchangeOpt.isPresent()) {
                throw new DataAccessException("找不到兌換記錄");
            }

            BonusExchangeBean exchange = exchangeOpt.get();

            // 退還點數
            CustomerBean customer = exchange.getCustomer();
            if (customer == null) {
                throw new DataAccessException("找不到會員資料");
            }
            customer.setTotalPoints(customer.getTotalPoints() + exchange.getUsePoints());
            cusRepo.save(customer);

            // 恢復庫存
            ItemManagementBean item = exchange.getExchangeItem();
            if (item == null) {
                throw new DataAccessException("找不到商品資料");
            }
            item.setItemMaximum(item.getItemMaximum() + exchange.getNumberOfExchange());
            itemMgnRepo.save(item);

            // 記錄點數退還
            savePointsHistory(exchange, "refund");

            // 刪除記錄
            exRepo.deleteById(exchangeId);

        } catch (Exception e) {
            throw new DataAccessException("刪除兌換記錄失敗: " + e.getMessage());
        }
    }

    // 私有輔助方法
    private ItemMgnDTO convertToItemMgnDTO(ItemManagementBean item) {
        ItemMgnDTO dto = new ItemMgnDTO();
        dto.setItemId(item.getItemId());
        dto.setProductName(item.getProduct().getProductName());
        dto.setProductCategory(item.getProduct().getProductCategory());
        dto.setItemPoints(item.getItemPoints());
        dto.setItemMaximum(item.getItemMaximum());
        dto.setStartDate(item.getStartDate());
        dto.setEndDate(item.getEndDate());
        dto.setActive(item.isActive());
        return dto;
    }

    private BonusExchangeDTO convertToExchangeDTO(BonusExchangeBean exchange) {
        BonusExchangeDTO dto = new BonusExchangeDTO();
        dto.setExchangeId(exchange.getExchangeId());
        dto.setCustomerTel(exchange.getCustomerTel());
        dto.setItemId(exchange.getItemId());
        dto.setUsePoints(exchange.getUsePoints());
        dto.setNumberOfExchange(exchange.getNumberOfExchange());
        dto.setExchangeDate(exchange.getExchangeDate());

        if (exchange.getExchangeItem() != null &&
                exchange.getExchangeItem().getProduct() != null) {
            dto.setProductName(exchange.getExchangeItem().getProduct().getProductName());
        } else {
            dto.setProductName("商品資料不存在");
        }

        return dto;
    }

    private void validateExchangeItemStatus(ItemManagementBean item) throws DataAccessException {
        LocalDate now = LocalDate.now();

        if (!item.isActive()) {
            throw new DataAccessException("商品已下架");
        }
        if (now.isBefore(item.getStartDate())) {
            throw new DataAccessException("商品尚未開始兌換");
        }
        if (now.isAfter(item.getEndDate())) {
            throw new DataAccessException("商品兌換期已結束");
        }
    }

    private String generateNextExchangeId() throws DataAccessException {
        try {
            List<String> lastIds = exRepo.findLastExchangeId();
            String lastId = lastIds.isEmpty() ? "EX00000000" : lastIds.get(0);

            if (!lastId.matches("EX\\d{8}")) {
                return "EX00000001";
            }

            int nextNumber = Integer.parseInt(lastId.substring(2)) + 1;
            return String.format("EX%08d", nextNumber);

        } catch (Exception e) {
            throw new DataAccessException("生成兌換ID失敗: " + e.getMessage());
        }
    }

    private void savePointsHistory(BonusExchangeBean exchange, String type) {
        try {
            PointsHistoryBean history = new PointsHistoryBean();
            history.setCustomerTel(exchange.getCustomerTel());
            history.setExchangeId(exchange.getExchangeId());

            // 根據類型設置點數變動（use為負數，refund為正數）
            int pointsChange = type.equals("use") ?
                    -exchange.getUsePoints() : exchange.getUsePoints();

            history.setPointsChange(pointsChange);
            history.setTransactionDate(LocalDate.now());
            history.setTransactionType(type);

            pointsHistoryRepo.save(history);
        } catch (Exception e) {
            logger.severe("儲存點數歷史紀錄失敗: " + e.getMessage());
            // 這裡選擇記錄錯誤但不拋出異常，因為這是附加功能
            // 如果要更嚴格的處理，可以拋出異常讓事務回滾
        }
    }
}