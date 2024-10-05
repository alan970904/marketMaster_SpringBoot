package marketMaster.controller.restock;

import marketMaster.DTO.restock.SupplierDTO.SupplierInfoDTO;
import marketMaster.bean.restock.SuppliersBean;
import marketMaster.service.restock.SupplierProductsService;
import marketMaster.service.restock.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
@RequestMapping("/supplier")
public class SupplierController {
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private SupplierProductsService supplierProductsService;

    @GetMapping("/supplier")
    public String supplier(){
        return "/restock/supplier";
    }

    @GetMapping("/AllSuppliers")
    @ResponseBody
    public List<SupplierInfoDTO> getAllSuppliersWithAccounts() {
        return supplierService.getAllSuppliersWithAccounts();
    }



    @GetMapping("/findProductsBySupplierId")
    public String findProductsBySupplierId(@RequestParam("supplierId") String supplierId, Model model) {
        supplierProductsService.findProductsBySupplierId(supplierId);
        model.addAttribute("supplierProducts", supplierProductsService.findProductsBySupplierId(supplierId));
        return "/restock/supplierProducts";
    }



}
