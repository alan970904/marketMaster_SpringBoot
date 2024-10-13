package marketMaster.controller.restock;

import marketMaster.bean.restock.SupplierProductsBean;
import marketMaster.service.restock.SupplierProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
@Controller
@RequestMapping("/supplierProducts")
public class SupplierProductsController {

    @Autowired
    private SupplierProductsService supplierProductsService;

    @PostMapping("/addProduct")
    @ResponseBody
    public SupplierProductsBean addSupplierProduct(@RequestBody SupplierProductsBean supplierProductsBean) {
        return supplierProductsService.addSupplierProduct(supplierProductsBean);
    }
    @DeleteMapping("/deleteProduct")
    @ResponseBody
    public void deleteSupplierProduct(@RequestParam String supplierProductId) {
        System.out.println("supplierProductId: " + supplierProductId);
        supplierProductsService.deleteSupplierProduct(supplierProductId);
    }

    @PutMapping("updateProduct")
    @ResponseBody
    public void updateSupplierProduct(@RequestBody SupplierProductsBean supplierProductsBean) {
        System.out.println(supplierProductsBean);
        supplierProductsService.updateSupplierProduct(supplierProductsBean);
    }


}
