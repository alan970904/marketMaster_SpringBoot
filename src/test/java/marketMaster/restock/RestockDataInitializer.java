package marketMaster.restock;

import marketMaster.DTO.restock.PaymentDTO.RestockDetailPaymentDTO;
import marketMaster.DTO.restock.restock.RestockDTO;
import marketMaster.DTO.restock.restock.RestockDetailDTO;
import marketMaster.service.restock.PaymentService;
import marketMaster.service.restock.RestockDetailService;
import marketMaster.service.restock.RestockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@SpringBootTest
public class RestockDataInitializer {

    @Autowired
    PaymentService paymentService;

    @Autowired
    RestockService restockService;
    @Autowired
    RestockDetailService restockDetailService;
    @Test
    public void getPaymentDetailsBySupplierId(){
        String supplierId = "S001";
           List<RestockDetailPaymentDTO>  good=   paymentService.getPaymentDetailsBySupplierId(supplierId);
        System.out.println(good);
    }


}
