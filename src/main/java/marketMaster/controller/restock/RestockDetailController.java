package marketMaster.controller.restock;

import marketMaster.DTO.restock.RestockDetailViewDTO;
import marketMaster.DTO.restock.RestockDetailsDTO;
import marketMaster.bean.restock.RestockDetailsId;
import marketMaster.service.restock.RestockDetailRepository;
import marketMaster.service.restock.RestockService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private RestockDetailRepository restockDetailRepository;

    @GetMapping("/restockDetail")
    public String restockDetail() {
        return "/restock/restockDetailList";
    }

    @GetMapping("/getAllRestockDetails")
    @ResponseBody
    public List<RestockDetailViewDTO> getAllRestockDetails() {

        return restockService.getAllRestockDetail();
    }

    @DeleteMapping("/delete")
    @ResponseBody
    public ResponseEntity<String> deleteRestockDetail(@RequestParam String restockId, @RequestParam String productId) {
        RestockDetailsId id = new RestockDetailsId(restockId, productId);
        restockDetailRepository.deleteById(id);
        return ResponseEntity.ok("刪除成功！");
    }

    @PutMapping("/update")
    @ResponseBody
    public void updateRestockDetail(@RequestBody RestockDetailViewDTO restockDetailViewDTO) {
         restockService.updateRestockDetail(restockDetailViewDTO);
    }

    @GetMapping("/searchByDateRange")
    @ResponseBody
    public List<RestockDetailViewDTO> searchByDateRange(@RequestParam String startDate, @RequestParam String endDate) {
        return restockService.findRestockDetailsByDateRange(startDate, endDate);
    }


}
