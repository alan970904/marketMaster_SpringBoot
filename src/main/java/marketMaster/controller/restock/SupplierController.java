package marketMaster.controller.restock;

import marketMaster.DTO.restock.PaymentDTO.PaymentInsertDTO;
import marketMaster.DTO.restock.PaymentDTO.RestockDetailPaymentDTO;
import marketMaster.DTO.restock.SupplierDTO.SupplierInfoDTO;
import marketMaster.DTO.restock.SupplierDTO.SupplierProductDTO;
import marketMaster.DTO.restock.SupplierDTO.SupplierProductDetailDTO;
import marketMaster.bean.restock.SupplierAccountsBean;
import marketMaster.bean.restock.SuppliersBean;
import marketMaster.service.restock.PaymentService;
import marketMaster.service.restock.SupplierAccountsRepository;
import marketMaster.service.restock.SupplierProductsService;
import marketMaster.service.restock.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/supplier")
    public String supplier(){
        return "/restock/supplier";
    }

    @GetMapping("/AllSuppliers")
    @ResponseBody
    public Page<SupplierInfoDTO> getAllSuppliersWithAccounts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return supplierService.getAllSuppliersWithAccounts(pageable);
    }
    //
    @GetMapping("/findProductsBySupplierId")
    public String findProductsBySupplierId(@RequestParam("supplierId") String supplierId, Model model) {
        model.addAttribute("supplierId", supplierId); // 只傳遞 supplierId 給前端
        return "/restock/supplierProducts"; // 渲染靜態頁面
    }


    @GetMapping("/findProductsBySupplierIdPage")
    @ResponseBody
    public Page<SupplierProductDetailDTO> findProductsBySupplierIdPage(@RequestParam("supplierId") String supplierId,@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
      return   supplierProductsService.findProductsBySupplierIdPage(supplierId,pageable);
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

    @DeleteMapping
    @ResponseBody
    public void deleteSupplier(@RequestParam("supplierId") String supplierId) {
        supplierService.deleteSupplier(supplierId);
    }
    //  跳轉到 supplier 付款
    @GetMapping("/supplierPayments")
    public String getAllSuppliersPaymentBySupplierId(@RequestParam("supplierId") String supplierId, Model model){
     List<RestockDetailPaymentDTO> dto=   paymentService.getPaymentDetailsBySupplierId(supplierId);
        model.addAttribute("supplierPayments", dto);
        return "/restock/supplierPayments";
    }

    //插入付款＆付款明細
    @PostMapping("/insertPayment")
    @ResponseBody
    public void insertRestockData(@RequestBody List<PaymentInsertDTO> PaymentInsertDTOList, @RequestParam("supplierId") String supplierId) {
        System.out.println("有進來");
        System.out.println(supplierId);
        for (PaymentInsertDTO dto : PaymentInsertDTOList) {
            paymentService.insertPayment(dto, supplierId);
        }
    }

    @GetMapping("/getAllSupplierProduct")
    public String getAllSupplierProductBySupplierId(@RequestParam("supplierId") String supplierId, Model model) {
     List<SupplierProductDetailDTO> list=  supplierProductsService.getAllSupplierProductBySupplierId(supplierId);
     model.addAttribute("productList",list);
       return "/restock/supplierProducts";
    }










}
