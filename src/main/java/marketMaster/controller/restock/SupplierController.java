package marketMaster.controller.restock;

import marketMaster.DTO.restock.PaymentDTO.PaymentInsertDTO;
import marketMaster.DTO.restock.PaymentDTO.RestockDetailPaymentDTO;
import marketMaster.DTO.restock.SupplierDTO.SupplierInfoDTO;
import marketMaster.DTO.restock.SupplierDTO.SupplierProductDetailDTO;
import marketMaster.bean.restock.SupplierAccountsBean;
import marketMaster.bean.restock.SuppliersBean;
import marketMaster.config.ECPayConfig;
import marketMaster.service.restock.PaymentService;
import marketMaster.service.restock.SupplierAccountsRepository;
import marketMaster.service.restock.SupplierProductsService;
import marketMaster.service.restock.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    @Autowired
    private ECPayConfig ecPayConfig;

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



    // 準備跳轉到 ECPay 支付頁面
    @GetMapping("/prepareECPayPayment")
    public String prepareECPayPayment(@RequestParam("supplierId") String supplierId, Model model) {
        // 獲取最新的支付記錄
        PaymentInsertDTO paymentInsertDTO = paymentService.getLatestPaymentInsertDTO(supplierId);
        int totalAmount = paymentInsertDTO.getTotalAmount();
        String merchantTradeNo = paymentInsertDTO.getMerchantTradeNo(); // 這裡是 payment_id

        // 準備 ECPay 所需的參數
        Map<String, String> ecpayParams = new HashMap<>();
        ecpayParams.put("MerchantID", ecPayConfig.getMerchantId());
        ecpayParams.put("MerchantTradeNo", merchantTradeNo); // 使用 payment_id 作為 MerchantTradeNo
        ecpayParams.put("MerchantTradeDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
        ecpayParams.put("PaymentType", "aio");
        ecpayParams.put("TotalAmount", String.valueOf(totalAmount));
        ecpayParams.put("TradeDesc", "供應商付款");
        ecpayParams.put("ItemName", "供應商付款"); // 根據實際需求設置
        ecpayParams.put("ReturnURL", ecPayConfig.getReturnUrl());
        ecpayParams.put("ChoosePayment", "WebATM"); // 根據需求選擇支付方式
        ecpayParams.put("EncryptType", "1");
        ecpayParams.put("ClientBackURL", ecPayConfig.getClientBackUrl());
        ecpayParams.put("ItemURL", "https://www.yourdomain.com/item");
        ecpayParams.put("Remark", "Additional notes");
        ecpayParams.put("OrderResultURL", ecPayConfig.getOrderResultUrl());
        ecpayParams.put("NeedExtraPaidInfo", "Y");
        ecpayParams.put("PlatformID", "");
        ecpayParams.put("CustomField1", "Custom value 1");
        ecpayParams.put("CustomField2", "Custom value 2");
        ecpayParams.put("CustomField3", "Custom value 3");
        ecpayParams.put("CustomField4", "Custom value 4");
        // 生成 CheckMacValue
        String checkMacValue = paymentService.generateCheckMacValue(ecpayParams);
        ecpayParams.put("CheckMacValue", checkMacValue);

        // 將參數傳遞給視圖
        model.addAttribute("ecpayParams", ecpayParams);
        model.addAttribute("ecpayUrl", "https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5"); // 測試環境

        return "/restock/ecpayPayment"; // 跳轉到包含支付表單的視圖
    }

    @PostMapping("/ecpayReturn")
    @ResponseBody
    public String handleECPayReturn(@RequestParam Map<String, String> params) {
        boolean isValid = paymentService.verifyECPayReturn(params);
        if (isValid) {
            String merchantTradeNo = params.get("MerchantTradeNo"); // payment_id
            String paymentStatus = params.get("RtnCode");

            if ("1".equals(paymentStatus)) {
                paymentService.updatePaymentStatus(merchantTradeNo, "已支付");
            } else {
                paymentService.updatePaymentStatus(merchantTradeNo, "支付失敗");
            }
            // 根據綠界支付的要求，返回 1|OK 表示成功收到通知
            return "1|OK";
        } else {
            // 返回 ECPay 認證失敗訊息
            return "0|FAIL";
        }
    }
}