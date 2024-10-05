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

    //刪除restockDetail中 detailId 並且更新restock表內 restockTotalPrice 金額
    public  void deleteRestockDetailAndUpdateTotalPrice(@RequestParam String detailId){
      RestockDetailsBean restockDetail = restockDetailsRepository.getById(detailId);
      RestocksBean restocks=restockDetail.getRestock();
      restockDetailsRepository.deleteById(detailId);
      int newTotalPrice=0;
      for (RestockDetailsBean detail:restocks.getRestockDetails()){
          newTotalPrice+=detail.getRestockTotalPrice();
      }
      restocks.setRestockTotalPrice(newTotalPrice);
      restocksRepository.save(restocks);
    }


}
