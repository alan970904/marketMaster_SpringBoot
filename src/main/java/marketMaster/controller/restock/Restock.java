package marketMaster.controller.restock;

import marketMaster.DTO.restock.RestockDTO;
import marketMaster.bean.restock.RestockBean;
import marketMaster.service.restock.RestockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Restock {



    @Autowired
    private RestockService restockService;

    @PostMapping("/insert")
    public ResponseEntity<String> insertRestockData(@RequestBody RestockDTO restockDTO) {
        try {
            restockService.insertRestockData(restockDTO);
            return new ResponseEntity<>("進貨資料插入成功。", HttpStatus.CREATED);
        } catch (Exception e) {
            // 可以在這裡記錄日誌
            return new ResponseEntity<>("進貨資料插入失敗。", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
