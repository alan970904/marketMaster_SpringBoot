package marketMaster.restock;

import marketMaster.DTO.restock.PaymentDTO.RestockDetailPaymentDTO;
import marketMaster.service.restock.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
@SpringBootTest
public class RestockDataInitializer {

    @Autowired
    PaymentService paymentService;
    @Test
    public void getPaymentDetailsBySupplierId(){
        String supplierId = "S001";
           List<RestockDetailPaymentDTO>  good=   paymentService.getPaymentDetailsBySupplierId(supplierId);
        System.out.println(good);
    }
}
