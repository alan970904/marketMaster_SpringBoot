package marketMaster.service.bonus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import marketMaster.bean.bonus.BonusExchangeBean;
import marketMaster.bean.product.ProductBean;
import marketMaster.bean.customer.CustomerBean;
import marketMaster.DTO.bonus.BonusExchangeDTO;
import marketMaster.DTO.bonus.ProductExchangeDTO;
import marketMaster.DTO.bonus.CustomerPointsDTO;
import marketMaster.exception.DataAccessException;
import marketMaster.service.bonus.BonusExchangeRepository;
import marketMaster.service.bonus.BonusProductRepository;
import marketMaster.service.bonus.BonusCustomerRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BonusExchangeService {
    @Autowired
    private BonusExchangeRepository bonusExchangeRepository;
    @Autowired
    private BonusProductRepository productRepository;
    @Autowired
    private BonusCustomerRepository customerRepository;
    
    private ProductExchangeDTO convertToProductExchangeDTO(ProductBean product) {
        return new ProductExchangeDTO(
            product.getProductId(),
            product.getProductName(),
            product.getProductCategory(),
            product.getProductPrice(),
            product.getNumberOfInventory()
        );
    }
    private BonusExchangeDTO convertToBonusExchangeDTO(BonusExchangeBean bean) {
        return new BonusExchangeDTO(
            bean.getExchangeId(),
            bean.getCustomerTel(),
            bean.getProductId(),
            bean.getProduct().getProductName(),
            bean.getUsePoints(),
            bean.getNumberOfExchange(),
            bean.getExchangeDate()
        );
    }
    public List<ProductExchangeDTO> getExchangeableProducts(String customerTel) throws DataAccessException {
        try {
            int customerPoints = customerRepository.getCustomerPoints(customerTel);
            List<ProductBean> products = productRepository.findExchangeableProducts(customerPoints);
            return products.stream().map(this::convertToProductExchangeDTO).collect(Collectors.toList());
        } catch (Exception e) {
            throw new DataAccessException("獲取可兌換商品失敗: " + e.getMessage(), e);
        }
    }
    public List<BonusExchangeDTO> getExchangeRecords(String customerTel) throws DataAccessException {
        try {
            List<BonusExchangeBean> records = bonusExchangeRepository.findByCustomerTel(customerTel);
            return records.stream().map(this::convertToBonusExchangeDTO).collect(Collectors.toList());
        } catch (Exception e) {
            throw new DataAccessException("獲取兌換記錄失敗: " + e.getMessage(), e);
        }
    }
    public CustomerPointsDTO getCustomerPoints(String customerTel) throws DataAccessException {
        try {
            CustomerBean customer = customerRepository.findById(customerTel)
                .orElseThrow(() -> new DataAccessException("客戶不存在"));
            return new CustomerPointsDTO(
                customer.getCustomerTel(),
                customer.getCustomerName(),
                customer.getTotalPoints()
            );
        } catch (Exception e) {
            throw new DataAccessException("獲取客戶積分失敗: " + e.getMessage(), e);
        }
    }
    public List<ProductExchangeDTO> getProductsByCategory(String category) throws DataAccessException {
        try {
            List<ProductBean> products = productRepository.findByProductCategory(category);
            return products.stream().map(this::convertToProductExchangeDTO).collect(Collectors.toList());
        } catch (Exception e) {
            throw new DataAccessException("獲取商品列表失敗: " + e.getMessage(), e);
        }
    }
    public ProductExchangeDTO getProductById(String productId) throws DataAccessException {
        try {
            ProductBean product = productRepository.findById(productId)
                .orElseThrow(() -> new DataAccessException("商品不存在"));
            return convertToProductExchangeDTO(product);
        } catch (Exception e) {
            throw new DataAccessException("獲取商品詳細信息失敗: " + e.getMessage(), e);
        }
    }
    
    @Transactional
    public void executeExchange(String customerTel, String productId, int exchangeQuantity) throws DataAccessException {
        try {
            if (exchangeQuantity <= 0) {
                throw new DataAccessException("兌換數量必須大於0");
            }
            int customerPoints = customerRepository.getCustomerPoints(customerTel);
            ProductBean product = productRepository.findById(productId)
                .orElseThrow(() -> new DataAccessException("商品不存在"));
            int requiredPoints = product.getProductPrice() * exchangeQuantity;
            if (customerPoints < requiredPoints) {
                throw new DataAccessException("積分不足");
            }
            if (product.getNumberOfInventory() < exchangeQuantity) {
                throw new DataAccessException("商品庫存不足");
            }
            customerRepository.updateCustomerPoints(customerTel, customerPoints - requiredPoints);
            int newInventory = product.getNumberOfInventory() - exchangeQuantity;
            productRepository.updateProductInventory(productId, newInventory);
            BonusExchangeBean exchange = new BonusExchangeBean();
            exchange.setExchangeId(generateNextExchangeId());
            exchange.setCustomerTel(customerTel);
            exchange.setProductId(productId);
            exchange.setUsePoints(requiredPoints);
            exchange.setNumberOfExchange(exchangeQuantity);
            exchange.setExchangeDate(LocalDate.now());
            bonusExchangeRepository.save(exchange);

        } catch (Exception e) {
            throw new DataAccessException("執行兌換失敗: " + e.getMessage(), e);
        }
    }
    
    
    @Transactional
    public void updateExchangeRecord(String exchangeId, String productId, int usePoints, int numberOfExchange) throws DataAccessException {
        try {
            Optional<BonusExchangeBean> optionalExchangeRecord = bonusExchangeRepository.findByExchangeId(exchangeId);
            if (!optionalExchangeRecord.isPresent()) {
                throw new DataAccessException("兌換記錄不存在");
            }
            BonusExchangeBean exchangeRecord = optionalExchangeRecord.get();
            int customerPoints = customerRepository.getCustomerPoints(exchangeRecord.getCustomerTel());
            int pointsDifference = usePoints - exchangeRecord.getUsePoints();
            int quantityDifference = numberOfExchange - exchangeRecord.getNumberOfExchange();
            if (customerPoints < pointsDifference) {
                throw new DataAccessException("積分不足，無法完成更新");
            }
            customerRepository.updateCustomerPoints(exchangeRecord.getCustomerTel(), 
                customerPoints - pointsDifference);

            Optional<ProductBean> optionalProduct = productRepository.findById(productId);
            if (!optionalProduct.isPresent()) {
                throw new DataAccessException("商品不存在");
            }
            ProductBean product = optionalProduct.get();
            if (product.getNumberOfInventory() < quantityDifference) {
                throw new DataAccessException("商品庫存不足，無法完成更新");
            }
            productRepository.updateProductInventory(productId, product.getNumberOfInventory() - quantityDifference);
            exchangeRecord.setProductId(productId);
            exchangeRecord.setUsePoints(usePoints);
            exchangeRecord.setNumberOfExchange(numberOfExchange);
            exchangeRecord.setExchangeDate(LocalDate.now());
            bonusExchangeRepository.save(exchangeRecord);
        } catch (Exception e) {
            throw new DataAccessException("更新兌換記錄失敗: " + e.getMessage(), e);
        }
    }
    
    @Transactional
    public void deleteExchangeRecord(String exchangeId) throws DataAccessException {
        try {
            Optional<BonusExchangeBean> optionalExchangeRecord = bonusExchangeRepository.findByExchangeId(exchangeId);
            if (!optionalExchangeRecord.isPresent()) {
                throw new DataAccessException("兌換記錄不存在");
            }
            BonusExchangeBean exchangeRecord = optionalExchangeRecord.get();

            customerRepository.updateCustomerPoints(exchangeRecord.getCustomerTel(), 
                customerRepository.getCustomerPoints(exchangeRecord.getCustomerTel()) + exchangeRecord.getUsePoints());

            Optional<ProductBean> optionalProduct = productRepository.findById(exchangeRecord.getProductId());
            if (!optionalProduct.isPresent()) {
                throw new DataAccessException("商品不存在");
            }
            ProductBean product = optionalProduct.get();
            productRepository.updateProductInventory(exchangeRecord.getProductId(), 
                product.getNumberOfInventory() + exchangeRecord.getNumberOfExchange());

            bonusExchangeRepository.delete(exchangeRecord);
        } catch (Exception e) {
            throw new DataAccessException("刪除兌換記錄失敗: " + e.getMessage(), e);
        }
    }

    private String generateNextExchangeId() {
        String lastId = bonusExchangeRepository.findLastExchangeId()
            .stream().findFirst().orElse("H00000000");
        int nextNumber = Integer.parseInt(lastId.substring(1)) + 1;
        return String.format("H%08d", nextNumber);
    }
}