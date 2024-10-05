package marketMaster.controller.restock;

import marketMaster.bean.restock.SupplierProductsBean;
import marketMaster.service.restock.SupplierProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/supplierProducts")
public class SupplierProductsController {

    @Autowired
    private SupplierProductsService supplierProductsService;

    @PostMapping("/add")
    public SupplierProductsBean addSupplierProduct(@RequestBody SupplierProductsBean supplierProductsBean) {
        return supplierProductsService.addSupplierProduct(supplierProductsBean);
    }



}
