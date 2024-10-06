package marketMaster.controller.restock;

import marketMaster.DTO.restock.restock.RestockDTO;
import marketMaster.DTO.restock.restock.RestockDetailDTO;
import marketMaster.service.restock.RestockDetailService;
import marketMaster.service.restock.RestockDetailsRepository;
import marketMaster.service.restock.RestockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/restockDetail")
public class RestockDetailController {
    @Autowired
    private RestockService restockService;
    @Autowired
    private RestockDetailService restockDetailService;
    @Autowired
    private RestockDetailsRepository restockDetailsRepository;

    @GetMapping("/restockDetail")
    public String restockDetail() {
        return "restock/restockDetail";
    }

    //拿到所有進貨資資訊
    @GetMapping("/getAllRestocks")
    @ResponseBody
    public List<RestockDTO> getAllRestocks() {
        return restockDetailService.getAllRestocks();
    }
    @DeleteMapping("/deleteByRestockId")
    @ResponseBody
    public void deleteByRestockId(@RequestParam String restockId){
        restockService.deleteRestockData(restockId);
    }

    //透過進貨id拿到 進貨詳細資料

    @GetMapping("/getAllRestockIdDetail")
    public String getAllRestockIdDetail(@RequestParam String restockId, Model model) {
    List<RestockDetailDTO> details=     restockDetailService.findRestockDetailByRestockId(restockId);
    model.addAttribute("details", details);
         return "restock/restockIdDetail";
    }

    //刪除restockDetail中 detailId 並且更新restock表內 restockTotalPrice 金額
    @DeleteMapping("/deleteByRestockDetailId")
    public ResponseEntity<String> deleteByRestockDetailId(@RequestParam String detailId) {
        try {
            restockDetailService.deleteRestockDetailAndUpdateTotalPrice(detailId);
            return ResponseEntity.ok("刪除成功並更新總金額！");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("刪除失敗！");
        }
    }

    //更新進貨明細的進貨數量跟進貨價格
    @PutMapping("/updateRestockDetail")
    public ResponseEntity<String> updateRestockDetail(@RequestBody RestockDetailDTO restockDetailDTO) {
        try {
            restockDetailService.updateRestockDetailAndTotalPrice(restockDetailDTO);
            return ResponseEntity.ok("更新成功！");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新失敗！");
        }
    }




//    @DeleteMapping("/delete")
//    @ResponseBody
//    public ResponseEntity<String> deleteRestockDetail(@RequestParam String restockId, @RequestParam String productId) {
//        RestockDetailsId id = new RestockDetailsId(restockId, productId);
//        restockDetailRepository.deleteById(id);
//        return ResponseEntity.ok("刪除成功！");
//    }
//
//    @PutMapping("/update")
//    @ResponseBody
//    public void updateRestockDetail(@RequestBody RestockDetailDTO restockDetailDTO) {
//         restockService.updateRestockDetail(restockDetailDTO);
//    }
//
//    @GetMapping("/searchByDateRange")
//    @ResponseBody
//    public List<RestockDetailDTO> searchByDateRange(@RequestParam String startDate, @RequestParam String endDate) {
//        return restockService.findRestockDetailsByDateRange(startDate, endDate);
//    }


}
