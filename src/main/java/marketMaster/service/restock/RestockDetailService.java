package marketMaster.service.restock;

import marketMaster.DTO.restock.restock.RestockDTO;
import marketMaster.DTO.restock.restock.RestockDetailDTO;
import marketMaster.bean.restock.RestockDetailsBean;
import marketMaster.bean.restock.RestocksBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
@Service
public class RestockDetailService {

    @Autowired
    private RestockDetailsRepository restockDetailsRepository;
    @Autowired
    private RestocksRepository restocksRepository;

    //  拿到最新DetailId
    public String getLastedDetailId() {
        // 找到最新的 detailId
        Optional<RestockDetailsBean> latestDetail = restockDetailsRepository.findTopByOrderByDetailIdDesc();

        String newDetailId = "RD001"; // 預設的初始值

        if (latestDetail.isPresent()) {
            String latestId = latestDetail.get().getDetailId(); // 獲取目前最大的 detailId

            // 提取 ID 中的數字部分，去掉前面的 "RD"
            int numericPart = Integer.parseInt(latestId.substring(2));

            // 數字部分加 1
            numericPart++;

            // 格式化為三位數字，並加上 "RD" 前綴
            newDetailId = String.format("RD%03d", numericPart);
        }

        return newDetailId;
    }

    //拿到所有進貨id資訊
    public List<RestockDTO> getAllRestocks() {
        return restocksRepository.findAllRestocks();
    }

    //透過進貨id拿到所有進貨明細
    public List<RestockDetailDTO>findRestockDetailByRestockId(@RequestParam String restockId){
        return restockDetailsRepository.findRestockDetailByRestockId(restockId);
    }

    // 更新進貨明細並更新進貨總金額
    public void updateRestockDetailAndTotalPrice(RestockDetailDTO restockDetailDTO) {
        // 1. 更新進貨明細
        restockDetailsRepository.updateRestockNumberAndPrice(
                restockDetailDTO.getNumberOfRestock(),
                restockDetailDTO.getProductPrice(),
                restockDetailDTO.getRestockTotalPrice(),
                restockDetailDTO.getDetailId()
        );

        // 2. 更新總金額
        updateRestockTotalPrice(restockDetailDTO.getRestockId());
    }

    // 刪除明細並更新總金額
    public void deleteRestockDetailAndUpdateTotalPrice(String detailId) {
        // 1. 查找進貨明細
        RestockDetailsBean restockDetail = restockDetailsRepository.getById(detailId);
        String restockId = restockDetail.getRestock().getRestockId();

        // 2. 刪除進貨明細
        restockDetailsRepository.deleteById(detailId);

        // 3. 更新進貨總金額
        updateRestockTotalPrice(restockId);
    }

    // 根據進貨編號更新總金額
    private void updateRestockTotalPrice(String restockId) {
        // 1. 查找所有該進貨編號的明細，計算新的總金額
        List<RestockDetailsBean> details = restockDetailsRepository.findByRestock_RestockId(restockId);
        int newTotalPrice = 0;
        for (RestockDetailsBean detail : details) {
            newTotalPrice += detail.getRestockTotalPrice();
        }
        // 2. 更新進貨表的總金額
        restocksRepository.updateRestocksTotalPrice(restockId, newTotalPrice);
    }
}


