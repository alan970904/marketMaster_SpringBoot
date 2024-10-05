package marketMaster.controller.restock;

import marketMaster.DTO.employee.EmployeeInfoDTO;
import marketMaster.DTO.restock.SupplierDTO.SupplierIdAndNameDTO;
import marketMaster.DTO.restock.SupplierDTO.SupplierProductDTO;
import marketMaster.DTO.restock.restock.RestockInsertDTO;
import marketMaster.bean.restock.SupplierProductsBean;
import marketMaster.service.restock.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/restock")
public class RestockInsertController {
    @Autowired
    RestockService restockService;
    @Autowired
    SupplierProductsService supplierProductsService;
    @Autowired
    SupplierService supplierService;
    @Autowired
    RestockDetailService restockDetailService;

    @GetMapping("/restockInsert")
    public String restockPage(){
        return "restock/restockInsert";
    }
    @GetMapping("/test")
    public String restockPage1(){
        return "restock/test";
    }

    @GetMapping("/getEmployeeInfo")
    @ResponseBody
    public List<EmployeeInfoDTO> getEmployeeInfo(){
        return restockService.getEmployeeInfo();
    }

    @GetMapping("/getSupplier")
    @ResponseBody
    public List<SupplierIdAndNameDTO> findAllSupplierIdAndName(){
       return supplierService.findAllSupplierIdAndName();
    }

    @GetMapping("/getProducts")
    @ResponseBody
    public List<SupplierProductDTO>findProductsBySupplierId(@RequestParam String supplierId){
        return supplierProductsService.findProductsBySupplierId(supplierId);
    }
    @GetMapping("/getLatestRestockId")
    @ResponseBody
    public String getLatestRestockId(){
        return restockService.getLatestRestockId();
    }
    @GetMapping("/getLastedDetailId")
    @ResponseBody
    public String getLastedDetailId(){
        return restockDetailService.getLastedDetailId();
    }

    @GetMapping("/getProductPrice")
    @ResponseBody
    public ResponseEntity<?> getProductPrice(@RequestParam String supplierId, @RequestParam String productId){
        Optional<Integer> productPriceOpt = supplierProductsService.getProductPrice(supplierId, productId);

        if(productPriceOpt.isPresent()){
            Integer productPrice = productPriceOpt.get();
            return ResponseEntity.ok(productPrice);
        } else {
            return ResponseEntity.badRequest().body("Invalid Supplier ID or Product ID");
        }
    }

    @PostMapping("/InsertRestock")
    @ResponseBody
    public void insertRestockData(@RequestBody List<RestockInsertDTO> restockInsertDTOList){
        for (RestockInsertDTO dto : restockInsertDTOList) {
            restockService.insertRestockData(dto);
        }
    }



}



