package marketMaster.service.bonus;

import marketMaster.bean.bonus.PointsHistoryBean;
import marketMaster.DTO.bonus.PointsHistoryDTO;
import marketMaster.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
//import java.util.stream.Collectors;

@Service
@Transactional
public class PointsHistoryService {

    @Autowired
    private PointsHistoryRepository pointsHistoryRepository;
    
    @Autowired
    private EncryptionService encryptionService;

    private PointsHistoryDTO convertToDTO(PointsHistoryBean bean) {
        return new PointsHistoryDTO(
            bean.getPointsHistoryId(),
            bean.getCustomerTel(),
            bean.getCheckoutId(),
            bean.getExchangeId(),
            bean.getPointsChange(),
            bean.getTransactionDate(),
            bean.getTransactionType(),
            encryptionService.encrypt(bean.getCustomerTel()),
            bean.getCustomer().getCustomerName() // 假設 Customer 實體有 getCustomerName 方法
        );
    }



//    public List<PointsHistoryDTO> getAllPointsHistories() throws DataAccessException {
//        try {
//            return pointsHistoryRepository.findAll().stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//        } catch (Exception e) {
//            throw new DataAccessException("獲取所有點數歷史記錄失敗: " + e.getMessage(), e);
//        }
//    }
//    public List<PointsHistoryDTO> getAllPointsHistories() {
//        List<PointsHistoryBean> beans = pointsHistoryRepository.findAll();
//        return beans.stream().map(this::convertToDTO).collect(Collectors.toList());
//    }
    public List<PointsHistoryDTO> getAllPointsHistories() {
        List<PointsHistoryBean> beans = pointsHistoryRepository.findAll();
        List<PointsHistoryDTO> dtoList = new ArrayList<>();
        for (PointsHistoryBean bean : beans) {
            dtoList.add(convertToDTO(bean));
        }
        return dtoList;
    }

//

    public List<PointsHistoryDTO> getPointsHistoriesByCustomerTel(String customerTel) throws DataAccessException {
        try {
            List<PointsHistoryBean> beans = pointsHistoryRepository.findByCustomerTel(customerTel);
            List<PointsHistoryDTO> dtoList = new ArrayList<>();
            for (PointsHistoryBean bean : beans) {
                dtoList.add(convertToDTO(bean));
            }
            return dtoList;
        } catch (Exception e) {
            throw new DataAccessException("獲取客戶點數歷史記錄失敗: " + e.getMessage(), e);
        }
    }

//    public PointsHistoryDTO getPointsHistoryById(Integer id) throws DataAccessException {
//        try {
//            Optional<PointsHistoryBean> pointsHistory = pointsHistoryRepository.findById(id);
//            return pointsHistory.map(this::convertToDTO)
//                .orElseThrow(() -> new DataAccessException("找不到指定的點數歷史記錄"));
//        } catch (Exception e) {
//            throw new DataAccessException("獲取點數歷史記錄失敗: " + e.getMessage(), e);
//        }
//    }
public PointsHistoryDTO getPointsHistoryById(Integer id) throws DataAccessException {
    try {
        Optional<PointsHistoryBean> pointsHistory = pointsHistoryRepository.findById(id);
        if (pointsHistory.isPresent()) {
            return convertToDTO(pointsHistory.get());
        } else {
            throw new DataAccessException("找不到指定的點數歷史記錄");
        }
    } catch (Exception e) {
        throw new DataAccessException("獲取點數歷史記錄失敗: " + e.getMessage(), e);
    }
}
//
public PointsHistoryDTO createPointsHistory(PointsHistoryDTO pointsHistoryDTO) throws DataAccessException {
    try {
        PointsHistoryBean bean = new PointsHistoryBean();
        bean.setCustomerTel(pointsHistoryDTO.getCustomerTel());
        bean.setCheckoutId(pointsHistoryDTO.getCheckoutId());
        bean.setExchangeId(pointsHistoryDTO.getExchangeId());
        bean.setPointsChange(pointsHistoryDTO.getPointsChange());
        bean.setTransactionDate(pointsHistoryDTO.getTransactionDate());
        bean.setTransactionType(pointsHistoryDTO.getTransactionType());

        PointsHistoryBean savedBean = pointsHistoryRepository.save(bean);
        return convertToDTO(savedBean);
    } catch (Exception e) {
        throw new DataAccessException("創建點數歷史記錄失敗: " + e.getMessage(), e);
    }
}

//    public PointsHistoryDTO updatePointsHistory(Integer id, PointsHistoryDTO pointsHistoryDTO) throws DataAccessException {
//        try {
//            Optional<PointsHistoryBean> existingBean = pointsHistoryRepository.findById(id);
//            if (!existingBean.isPresent()) {
//                throw new DataAccessException("找不到指定的點數歷史記錄");
//            }
//            PointsHistoryBean beanToUpdate = existingBean.get();
//            beanToUpdate.setCustomerTel(pointsHistoryDTO.getCustomerTel());
//            beanToUpdate.setCheckoutId(pointsHistoryDTO.getCheckoutId());
//            beanToUpdate.setExchangeId(pointsHistoryDTO.getExchangeId());
//            beanToUpdate.setPointsChange(pointsHistoryDTO.getPointsChange());
//            beanToUpdate.setTransactionDate(pointsHistoryDTO.getTransactionDate());
//            beanToUpdate.setTransactionType(pointsHistoryDTO.getTransactionType());
//
//            PointsHistoryBean updatedBean = pointsHistoryRepository.save(beanToUpdate);
//            return convertToDTO(updatedBean);
//        } catch (Exception e) {
//            throw new DataAccessException("更新點數歷史記錄失敗: " + e.getMessage(), e);
//        }
//    }
public PointsHistoryDTO updatePointsHistory(Integer id, PointsHistoryDTO pointsHistoryDTO) throws DataAccessException {
    try {
        Optional<PointsHistoryBean> existingBean = pointsHistoryRepository.findById(id);
        if (!existingBean.isPresent()) {
            throw new DataAccessException("找不到指定的點數歷史記錄");
        }

        PointsHistoryBean beanToUpdate = existingBean.get();
        beanToUpdate.setCustomerTel(pointsHistoryDTO.getCustomerTel());
        beanToUpdate.setCheckoutId(pointsHistoryDTO.getCheckoutId());
        beanToUpdate.setExchangeId(pointsHistoryDTO.getExchangeId());
        beanToUpdate.setPointsChange(pointsHistoryDTO.getPointsChange());
        beanToUpdate.setTransactionDate(pointsHistoryDTO.getTransactionDate());
        beanToUpdate.setTransactionType(pointsHistoryDTO.getTransactionType());

        PointsHistoryBean updatedBean = pointsHistoryRepository.save(beanToUpdate);
        return convertToDTO(updatedBean);
    } catch (Exception e) {
        throw new DataAccessException("更新點數歷史記錄失敗: " + e.getMessage(), e);
    }
}
    public void deletePointsHistory(Integer id) throws DataAccessException {
        try {
            if (!pointsHistoryRepository.existsById(id)) {
                throw new DataAccessException("找不到指定的點數歷史記錄");
            }
            pointsHistoryRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataAccessException("刪除點數歷史記錄失敗: " + e.getMessage(), e);
        }
    }


}