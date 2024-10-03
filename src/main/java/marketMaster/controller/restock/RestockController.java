package marketMaster.controller.restock;

import marketMaster.DTO.employee.EmployeeInfoDTO;
import marketMaster.DTO.product.ProductCategoryDTO;
import marketMaster.DTO.product.ProductIdDTO;
import marketMaster.DTO.product.ProductNameDTO;
import marketMaster.DTO.restock.RestockDTO;
import marketMaster.service.restock.RestockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/restock")
public class RestockController {
    @Autowired
    RestockService restockService;

    @GetMapping("/restockInsert")
    public String restockPage(){
        return "restock/restockInsert";
    }

    @GetMapping("/getEmployeeInfo")
    @ResponseBody
    public List<EmployeeInfoDTO> getEmployeeInfo(){
        return restockService.getEmployeeInfo();
    }
    @GetMapping("/getLatestRestockId")
    @ResponseBody
    public String getLatestRestockId(){
        return restockService.getLatestRestockId();
    }
    @GetMapping("/getProductCategory")
    @ResponseBody
    public List<ProductCategoryDTO> getProductCategory(){
        return restockService.getProductCategory();
    }

    @GetMapping("/getProductName")
    @ResponseBody
    public List<ProductNameDTO> getProductName(@RequestParam String productCategory){
        return  restockService.getAllProductNamesByCategory( productCategory);
    }
    @GetMapping("/getProductId")
    @ResponseBody
    public List<ProductIdDTO> findAllProductIdByProductName(@RequestParam String productName){
        return restockService.findAllProductIdByProductName(productName);
    }

    @PostMapping("/insertRestockData")
    @ResponseBody
    public ResponseEntity<Void> insertRestockData(@RequestBody RestockDTO restockDTO){
        try {
            restockService.insertRestockData(restockDTO);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
