package marketMaster.service.restock;


import marketMaster.DTO.employee.EmployeeInfoDTO;
import marketMaster.DTO.restock.restock.RestockDetailsInsertDTO;
import marketMaster.DTO.restock.restock.RestockInsertDTO;
import marketMaster.bean.employee.EmpBean;
import marketMaster.bean.restock.RestockDetailsBean;
import marketMaster.bean.restock.RestocksBean;
import marketMaster.bean.restock.SupplierProductsBean;
import marketMaster.bean.restock.SuppliersBean;
import marketMaster.service.employee.EmployeeRepository;
import marketMaster.service.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
    private SupplierAccountService supplierAccountService; ;

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
        EmpBean employeeId = employeeRepository.findById(restockInsertDTO.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Employee ID: " + restockInsertDTO.getEmployeeId()));

        RestocksBean insertDTO =   new RestocksBean();
    insertDTO.setRestockId(restockInsertDTO.getRestockId());
    insertDTO.setRestockTotalPrice(restockInsertDTO.getRestockTotalPrice());
    insertDTO.setRestockDate(restockInsertDTO.getRestockDate());
    insertDTO.setEmployee(employeeId);
    restocksRepository.save(insertDTO);

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
        detail.setRestockTotalPrice(rd.getRestockTotalPrice());
        detail.setProductionDate(rd.getProductionDate());
        detail.setDueDate(rd.getDueDate());
        detail.setRestockDate(LocalDate.now());
        detail.setRestock(insertDTO);
        restockDetailsRepository.save(detail);


        supplierAccountService.updateAccount(rd.getSupplierId(), rd.getRestockTotalPrice());

    }
    }

    public void deleteRestockData(@RequestParam String restockId) {
        restocksRepository.deleteById(restockId);
    }

    }





