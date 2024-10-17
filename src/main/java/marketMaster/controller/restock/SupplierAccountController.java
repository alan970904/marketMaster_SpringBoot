package marketMaster.controller.restock;

import marketMaster.bean.restock.SupplierAccountsBean;
import marketMaster.service.restock.SupplierAccountService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/SupplierAccount")
public class SupplierAccountController {
    private SupplierAccountService supplierAccountService;


}
