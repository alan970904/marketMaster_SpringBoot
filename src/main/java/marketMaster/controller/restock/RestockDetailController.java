package marketMaster.controller.restock;

import marketMaster.DTO.restock.restock.RestockDTO;
import marketMaster.DTO.restock.restock.RestockDetailDTO;
import marketMaster.service.restock.RestockDetailService;
import marketMaster.service.restock.RestockDetailsRepository;
import marketMaster.service.restock.RestockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
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
    public Page<RestockDTO> getAllRestocks(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return restockDetailService.getAllRestocks(pageable);
    }

    //刪除進貨編號
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
    @ResponseBody
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
    @ResponseBody
    public ResponseEntity<String> updateRestockDetail(@RequestBody RestockDetailDTO restockDetailDTO) {
        try {
            restockDetailService.updateRestockDetailAndTotalPrice(restockDetailDTO);
            return ResponseEntity.ok("更新成功！");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新失敗！");
        }
    }
        //  根據日期查找
    @GetMapping("/getRestockDetailsByDateRange")
    @ResponseBody
    public Page<RestockDTO> getRestockDetailsByDateRange(@RequestParam String startDate, @RequestParam String endDate,@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        Pageable pageable = PageRequest.of(page, size);
        return restockService.getRestockDetailsByDateRange(start, end,pageable);
    }

    //根據日期查詢匯出excel
    @GetMapping("/getExcelByDateRange")
    @ResponseBody
    public ResponseEntity<ByteArrayResource> getExcelByDateRange(@RequestParam String startDate, @RequestParam String endDate) throws IOException {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        byte[] excelBytes = restockService.exportRestockDetailsToExcel(start, end);
        ByteArrayResource resource = new ByteArrayResource(excelBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=restock_details.xlsx");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(excelBytes.length)
                .body(resource);

    }






}