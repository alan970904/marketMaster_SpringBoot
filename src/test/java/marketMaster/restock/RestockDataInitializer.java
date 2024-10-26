package marketMaster.restock;

import ecpay.payment.integration.AllInOne;
import ecpay.payment.integration.domain.AioCheckOutALL;
import marketMaster.DTO.restock.PaymentDTO.RestockDetailPaymentDTO;
import marketMaster.bean.restock.PaymentsBean;
import marketMaster.controller.restock.SupplierController;
import marketMaster.service.product.ProductRepository;
import marketMaster.service.restock.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test") // 啟用 test profile，排除 WebSocketConfig
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

    @Autowired
    SupplierController supplierController;

    @Autowired
    PaymentsRepository paymentsRepository;
    @Test
    public void getPaymentDetailsBySupplierId() {
        String supplierId = "S001";
        List<RestockDetailPaymentDTO> paymentDetails = paymentService.getPaymentDetailsBySupplierId(supplierId);
        System.out.println(paymentDetails);
    }

    @Test
    public void excel() throws IOException {
        // 設定日期範圍
        LocalDate startDate = LocalDate.of(2024, 10, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 3);

        // 調用服務層的方法，並獲取 Excel 的 byte[] 結果
        byte[] excelBytes = restockService.exportRestockDetailsToExcel(startDate, endDate);

        // 創建一個臨時文件來保存 Excel 結果
        File tempFile = File.createTempFile("restock_details_", ".xlsx");

        // 將 byte[] 寫入臨時文件
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(excelBytes);
        }

        // 驗證文件是否正確創建並有內容
        assertTrue(tempFile.exists());
        assertTrue(tempFile.length() > 0);

        // 可以進一步驗證文件內容，例如檢查第一行的標題是否正確
        try (FileInputStream fis = new FileInputStream(tempFile)) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            // 驗證標題欄位
            assertEquals("進貨編號", headerRow.getCell(0).getStringCellValue());
            assertEquals("員工編號與姓名", headerRow.getCell(1).getStringCellValue());
            assertEquals("進貨日期", headerRow.getCell(2).getStringCellValue());
            assertEquals("進貨總金額", headerRow.getCell(3).getStringCellValue());

            workbook.close();
        }

        // 刪除臨時文件
        tempFile.deleteOnExit();

        System.out.println("Excel 匯出成功: " + tempFile.getAbsolutePath());
    }


        @Test
        public void  ecpayCheckout() {

            ECpayAIO_Java.ecpay.payment.integration.AllInOne all = new ECpayAIO_Java.ecpay.payment.integration.AllInOne("");

            AioCheckOutALL obj = new AioCheckOutALL();
            obj.setMerchantTradeNo("testCompany0004");
            obj.setMerchantTradeDate("2017/01/01 08:05:23");
            obj.setTotalAmount("50");
            obj.setTradeDesc("test Description");
            obj.setItemName("TestItem");
            // 交易結果回傳網址，只接受 https 開頭的網站，可以使用 ngrok
            obj.setReturnURL("<http://211.23.128.214:5000>");
            obj.setNeedExtraPaidInfo("N");
            // 商店轉跳網址 (Optional)
            obj.setClientBackURL("<http://192.168.1.37:8080/>");
            String form = all.aioCheckOut(obj, null);

            System.out.println(form);
        }

        @Test
    public void  good() {
       String supplierId ="S002";
            PaymentsBean latestPayment = paymentsRepository.findTopBySupplierAccounts_Supplier_SupplierIdOrderByPaymentDateDesc(supplierId);
            System.out.println(latestPayment);
        }

        @Test
    public void  goo123(){
        String supplierId ="S003";
        paymentService.getPaymentDetailsBySupplierId(supplierId);
            }
        }




