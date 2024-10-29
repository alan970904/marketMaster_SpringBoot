package marketMaster.service.restock;


import marketMaster.DTO.employee.EmployeeInfoDTO;
import marketMaster.DTO.restock.restock.RestockDTO;
import marketMaster.DTO.restock.restock.RestockDetailDTO;
import marketMaster.DTO.restock.restock.RestockDetailsInsertDTO;
import marketMaster.DTO.restock.restock.RestockInsertDTO;
import marketMaster.bean.employee.EmpBean;
import marketMaster.bean.restock.RestockDetailsBean;
import marketMaster.bean.restock.RestocksBean;
import marketMaster.bean.restock.SupplierProductsBean;
import marketMaster.bean.restock.SuppliersBean;
import marketMaster.service.employee.EmployeeRepository;
import marketMaster.service.product.ProductRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RestockService {
    @Autowired
    private  RestocksRepository restocksRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private RestockDetailService restockDetailService;
    @Autowired
    private SupplierProductsRepository supplierProductsRepository;
    @Autowired
    private SuppliersRepository suppliersRepository;
    @Autowired
    private RestockDetailsRepository restockDetailsRepository;
    @Autowired
    private SupplierAccountsRepository accountsRepository;

    @Autowired
    private SupplierAccountService supplierAccountService; ;
    @Autowired
    private SupplierAccountsRepository accountRepository;

    public String getLatestRestockId() {
        // 取得當天的日期，格式為 YYYYMMDD
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

        // 使用 '%' + today + '%' 查詢所有今天的進貨 ID
        String restockIdPattern = "%" + today + "%";

        // 從資料庫中查詢最新的進貨 ID
        RestocksBean latestRestock = restocksRepository.findLatestRestockByDate(restockIdPattern).orElse(null);

        String newId;
        if (latestRestock != null) {
            // 如果找到最新的進貨 ID，解析出序號並增加 1
            String latestId = latestRestock.getRestockId();
            int sequence = Integer.parseInt(latestId.substring(8)) + 1;  // 提取 ID 中的序號部分
            newId = String.format("%s%03d", today, sequence);  // 格式化新的 ID，序號保持三位
        } else {
            // 如果今天還沒有進貨 ID，則從 "001" 開始
            newId = today + "001";
        }

        return newId;
    }

    /**
     * 取得所有員工資訊
     */
    public List<EmployeeInfoDTO> getEmployeeInfo() {
        return employeeRepository.findAllEmployeeInfo();
    }


    //插入進貨資料 並且更新 Account total
    @Transactional
    public void insertRestockData(RestockInsertDTO restockInsertDTO) {
        EmpBean employee = employeeRepository.findById(restockInsertDTO.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Employee ID: " + restockInsertDTO.getEmployeeId()));
        RestocksBean existingRestock =restocksRepository.findById(restockInsertDTO.getRestockId()).orElse(null);
        if (existingRestock != null) {
            existingRestock.setRestockTotalPrice(existingRestock.getRestockTotalPrice() + restockInsertDTO.getRestockTotalPrice());
            existingRestock.setEmployee(employee);
        } else {
            existingRestock = new RestocksBean();
            existingRestock.setRestockId(restockInsertDTO.getRestockId());
            existingRestock.setRestockTotalPrice(restockInsertDTO.getRestockTotalPrice());
            existingRestock.setRestockDate(restockInsertDTO.getRestockDate());
            existingRestock.setEmployee(employee);
        }
        restocksRepository.save(existingRestock);

    for (RestockDetailsInsertDTO rd : restockInsertDTO.getRestockDetails()) {
        RestockDetailsBean detail = new RestockDetailsBean();

        SuppliersBean supplier = suppliersRepository.findById(rd.getSupplierId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Supplier ID: " + rd.getSupplierId()));
        // 根据 supplierId 和 productId 查找 supplierProductId
        Optional<SupplierProductsBean> optionalSupplierProduct = supplierProductsRepository.findBySupplier_SupplierIdAndProduct_ProductId(rd.getSupplierId(), rd.getProductId());

        SupplierProductsBean supplierProduct = optionalSupplierProduct
                .orElseThrow(() -> new IllegalArgumentException("Invalid combination of Supplier ID: " + rd.getSupplierId() + " and Product ID: " + rd.getProductId()));

        detail.setDetailId(restockDetailService.getLastedDetailId());
        detail.setSupplier(supplier);
        detail.setSupplierProduct(supplierProduct);
        detail.setNumberOfRestock(rd.getNumberOfRestock());
        detail.setPriceAtRestock(rd.getPriceAtRestock());
        detail.setRestockTotalPrice(rd.getRestockTotalPrice());
        detail.setProductionDate(rd.getProductionDate());
        detail.setDueDate(rd.getDueDate());
        detail.setRestockDate(LocalDate.now());
        detail.setRestock(existingRestock);
        restockDetailsRepository.save(detail);


        supplierAccountService.updateAccount(rd.getSupplierId(), rd.getRestockTotalPrice());

    }
    }
    //刪除RestockId 並更新supplierTotalAmount
    @Transactional
    public void deleteRestockData(@RequestParam String restockId) {
        //透過restock找到detailId
        List<String> detailIds =restockDetailsRepository.findDetailIdByRestockId(restockId);
        //透過detailId找 supplier and restockTotalPrice
        List<Object[]> supplierData=restockDetailsRepository.findSupplierIdAndTotalPriceByDetailIds(detailIds);
        restocksRepository.deleteById(restockId);
        restocksRepository.deleteById(restockId); // 確保刪除操作先執行
        //記錄每個供應商的總金額
        Map<String,Integer>supplierAmountMap=new HashMap<>();
        for (Object[] data : supplierData) {
            String supplierId = (String) data[0];
            int restockTotalPrice = (Integer) data[1];
            supplierAmountMap.put(supplierId, supplierAmountMap.getOrDefault(supplierId, 0) + restockTotalPrice);
        }
        //更新每個供應商的進貨總金額
        for (String supplierId : supplierAmountMap.keySet()) {
            int newTotalAmount =0;
            List<RestockDetailsBean>details=restockDetailsRepository.findBySupplierId(supplierId);
            for (RestockDetailsBean detail : details) {
                newTotalAmount += detail.getRestockTotalPrice();
            }
            System.out.println("Supplier ID: " + supplierId + " New Total Amount: " + newTotalAmount);
            accountsRepository.updateSupplierTotalAmount(supplierId, newTotalAmount);
        }
        // 更新未付款金額
        for (String supplierId : supplierAmountMap.keySet()) {
            updateSupplierUnpaidAmount(supplierId); // 更新未付款金額
        }

    }


    //根據日期區間查詢
    public Page<RestockDTO> getRestockDetailsByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return restocksRepository.findRestockDetailByRestockDateBetween(startDate, endDate,pageable);
    }
        //匯出excel
        public byte[]exportRestockDetailsToExcel(LocalDate startDate, LocalDate endDate) throws IOException {
            List<RestockDTO> restocks = restocksRepository.findRestockDetailByRestockDateBetween(startDate, endDate, Pageable.unpaged()).getContent();
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Restock Details");
            Row headerRow = sheet.createRow(0);
            String[]headers={"進貨編號","員工編號與姓名","進貨日期","進貨總金額"};
            for(int i=0;i<headers.length;i++){
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            int rowIdx=1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (RestockDTO restock : restocks) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(restock.getRestockId());
                row.createCell(1).setCellValue(restock.getEmployeeId()+"-"+restock.getEmployeeName());
                row.createCell(2).setCellValue(restock.getRestockDate().format(formatter));
                row.createCell(3).setCellValue(restock.getRestockTotalPrice());
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            return outputStream.toByteArray();


        }
    private void updateSupplierUnpaidAmount(String supplierId){
        int totalAmount=  accountRepository.getTotalAmountBySupplierId(supplierId);
        int paidAmount =  accountRepository.getPaidAmountBySupplierId(supplierId);
        int newUnpaidAmount = totalAmount - paidAmount;
        System.out.println( "newUnpaidAmount"+newUnpaidAmount);
        accountRepository.updateSupplierUnpaidAmount(supplierId,newUnpaidAmount);
    }



}





