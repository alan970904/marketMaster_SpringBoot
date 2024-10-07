package marketMaster.controller.restock;

import marketMaster.DTO.restock.SupplierDTO.SupplierInfoDTO;
import marketMaster.bean.restock.SupplierAccountsBean;
import marketMaster.bean.restock.SuppliersBean;
import marketMaster.service.restock.SupplierAccountsRepository;
import marketMaster.service.restock.SupplierProductsService;
import marketMaster.service.restock.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/supplier")
public class SupplierController {
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private SupplierProductsService supplierProductsService;
    @Autowired
    private SupplierAccountsRepository supplierAccountsRepository;

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


    @PutMapping("/updateSupplier")
    @ResponseBody
    public String updateSupplier(@RequestBody SupplierInfoDTO supplierInfoDTO) {
      return   supplierService.updateSupplierBySupplierId(supplierInfoDTO);

    }


    @PostMapping("/addSupplier")
    @ResponseBody
    public String addSupplier(@RequestBody SuppliersBean suppliersBean) {
        String newSupplierId=supplierService.getLastSupplierId();
        suppliersBean.setSupplierId(newSupplierId);
        supplierService.saveSupplier(suppliersBean);

        SupplierAccountsBean accountsBean=new SupplierAccountsBean();
        accountsBean.setAccountId("ACC"+newSupplierId.substring(1));
        accountsBean.setSupplier(suppliersBean);
        accountsBean.setTotalAmount(0);
        accountsBean.setUnpaidAmount(0);
        accountsBean.setPaidAmount(0);
        suppliersBean.setSupplierAccount(accountsBean);
        supplierAccountsRepository.save(accountsBean);


        return newSupplierId;
    }

    @GetMapping("/getSupplierId")
    @ResponseBody
    public String getSupplierId(){
      return   supplierService.getLastSupplierId();
    }








}
