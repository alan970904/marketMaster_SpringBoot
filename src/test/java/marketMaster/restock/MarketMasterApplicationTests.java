package marketMaster.restock;

import marketMaster.DTO.employee.EmployeeInfoDTO;
import marketMaster.DTO.product.ProductCategoryDTO;
import marketMaster.DTO.product.ProductNameDTO;
import marketMaster.DTO.restock.RestockDTO;
import marketMaster.DTO.restock.RestockDetailViewDTO;
import marketMaster.DTO.restock.RestockDetailsDTO;
import marketMaster.bean.restock.RestockBean;
import marketMaster.bean.restock.RestockDetailsBean;
import marketMaster.service.restock.RestockDetailRepository;
import marketMaster.service.restock.RestockRepository;
import marketMaster.service.restock.RestockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MarketMasterApplicationTests {

    @Autowired
    private RestockService restockService;

    @Autowired
    private RestockRepository restockRepository;

    @Autowired
    private RestockDetailRepository restockDetailRepository;

    @Test
    void contextLoads() {
        // 確保應用程式上下文能夠正確加載
    }

    @Test
    @Transactional
    public void getLatestRestockId() {
        String latestRestockId = restockService.getLatestRestockId();
        System.out.println("Latest Restock ID: " + latestRestockId);
        assertNotNull(latestRestockId, "Latest Restock ID should not be null");
        assertTrue(latestRestockId.matches("\\d{8}\\d{3}"), "Restock ID should match the format YYYYMMDDXXX");
    }

    @Test
    @Transactional
    public void testEmployeeInfo(){
        List<EmployeeInfoDTO> employeeInfos = restockService.getEmployeeInfo();
        assertNotNull(employeeInfos, "Employee info list should not be null");
        assertEquals(6, employeeInfos.size(), "There should be 6 employees");
        employeeInfos.forEach(System.out::println);
    }

    @Test
    @Transactional
    public void testGetProductCategory(){
        List<ProductCategoryDTO> productCategories = restockService.getProductCategory();
        assertNotNull(productCategories, "Product categories list should not be null");
        assertFalse(productCategories.isEmpty(), "Product categories should not be empty");
        productCategories.forEach(System.out::println);
    }

    @Test
    @Transactional
    public void testGetProductName(){
        String productCategory = "飲品";
        List<ProductNameDTO> productNames = restockService.getAllProductNamesByCategory(productCategory);
        assertNotNull(productNames, "Product names list should not be null");
        assertFalse(productNames.isEmpty(), "Product names should not be empty for category: " + productCategory);
        productNames.forEach(System.out::println);
    }

    @Test
    @Transactional
    public void testInsertRestockData() {
        // 準備進貨資料
        RestockDTO restockDTO = new RestockDTO();
        restockDTO.setRestockId("20240926001");
        restockDTO.setEmployeeId("E001");
        restockDTO.setRestockTotalPrice(1500);
        restockDTO.setRestockDate(LocalDate.of(2024, 9, 26));

        RestockDetailsDTO detail1 = new RestockDetailsDTO();
        detail1.setProductId("PDR003");
        detail1.setNumberOfRestock(100);
        detail1.setProductName("伯朗咖啡");
        detail1.setProductPrice(10);
        detail1.setRestockTotalPrice(1000);
        detail1.setProductionDate(LocalDate.of(2024, 9, 1));
        detail1.setDueDate(LocalDate.of(2025, 9, 1));
        detail1.setRestockDate(LocalDate.of(2024, 9, 26));

        restockDTO.setRestockDetails(List.of(detail1));

        // 呼叫服務方法插入進貨資料
        restockService.insertRestockData(restockDTO);

        // 驗證進貨資料是否已正確插入
        Optional<RestockBean> insertedRestockOpt = restockRepository.findById("20240926001");
        assertTrue(insertedRestockOpt.isPresent(), "Inserted Restock should be present");

        RestockBean insertedRestock = insertedRestockOpt.get();
        assertEquals("E001", insertedRestock.getEmployee().getEmployeeId(), "Employee ID should be 'E001'");
        assertEquals(1500, insertedRestock.getRestockTotalPrice(), "Restock Total Price should be 1500");
        assertEquals(LocalDate.of(2024, 9, 26), insertedRestock.getRestockDate(), "Restock Date should be 2024-09-26");

        // 進一步驗證進貨詳情
        List<RestockDetailsBean> restockDetails = insertedRestock.getRestockDetails();
        assertEquals(1, restockDetails.size(), "There should be 1 restock detail");

        RestockDetailsBean insertedDetail = restockDetails.get(0);
        assertEquals("PDR003", insertedDetail.getProduct().getProductId(), "Product ID should be 'PDR003'");
        assertEquals(100, insertedDetail.getNumberOfRestock(), "Number of restock should be 100");
        assertEquals("伯朗咖啡", insertedDetail.getProductName(), "Product Name should be '伯朗咖啡'");
        assertEquals(10, insertedDetail.getProductPrice(), "Product Price should be 10");
        assertEquals(1000, insertedDetail.getRestockTotalPrice(), "Restock Total Price should be 1000");
        assertEquals(LocalDate.of(2024, 9, 1), insertedDetail.getProductionDate(), "Production Date should be 2024-09-01");
        assertEquals(LocalDate.of(2025, 9, 1), insertedDetail.getDueDate(), "Due Date should be 2025-09-01");
        assertEquals(LocalDate.of(2024, 9, 26), insertedDetail.getRestockDate(), "Restock Date should be 2024-09-26");

        // 驗證 `getLatestRestockId()` 返回下一個 ID
        String latestRestockId = restockService.getLatestRestockId();
        assertEquals("20240928001", latestRestockId, "Latest Restock ID should be '20240928001'");
    }
    @Test
    public void testGetALl(){
      List<RestockDetailViewDTO> restockDetailViewDTOList=  restockService.getAllRestockDetail();
      restockDetailViewDTOList.forEach(System.out::println);

    }
    @Test
    public void testDelete(){
        restockService.delete("20240928001");
    }


}
