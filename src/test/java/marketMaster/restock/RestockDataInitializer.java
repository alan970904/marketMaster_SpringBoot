package marketMaster.restock;

import marketMaster.DTO.restock.PaymentDTO.RestockDetailPaymentDTO;
import marketMaster.DTO.restock.SupplierDTO.SupplierProductDTO;
import marketMaster.DTO.restock.restock.RestockDTO;
import marketMaster.DTO.restock.restock.RestockDetailDTO;
import marketMaster.service.product.ProductRepository;
import marketMaster.service.restock.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@SpringBootTest
public class RestockDataInitializer {

    @Autowired
    PaymentService paymentService;

    @Autowired
    RestockService restockService;
    @Autowired
    RestockDetailService restockDetailService;
    @Autowired
    RestockDetailsRepository restockDetailsRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    SupplierService supplierService;
    @Autowired
    SupplierProductsService supplierProductsService;
    @Test
    public void getPaymentDetailsBySupplierId(){
        String supplierId = "S001";
           List<RestockDetailPaymentDTO>  good=   paymentService.getPaymentDetailsBySupplierId(supplierId);
        System.out.println(good);
    }

//    @Test
//    public void good(){
//        String supplierId = "PDR001";
//       List<LocalDate>a = productRepository.findProductionDatesByProductId(supplierId);
//        System.out.println(a);
//    }
    @Test
    public void goo1(){
        int page =0;
        int size =10;
        Pageable pageable = PageRequest.of(page, size);
       Page<SupplierProductDTO> goo= supplierProductsService.findProductsBySupplierIdPage("S001",pageable);
        System.out.println(goo);
    }

}
