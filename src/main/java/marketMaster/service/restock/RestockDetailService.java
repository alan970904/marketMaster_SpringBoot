package marketMaster.service.restock;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import marketMaster.DTO.restock.restock.RestockDTO;
import marketMaster.DTO.restock.restock.RestockDetailDTO;
import marketMaster.bean.restock.RestockDetailsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class RestockDetailService {


    private final RestockDetailsRepository restockDetailsRepository;
    private final RestocksRepository restocksRepository;
    private final SupplierAccountsRepository accountsRepository;
    private final SupplierProductsRepository supplierProductRepository;
    private final SupplierAccountsRepository supplierAccountsRepository;

    //  拿到最新DetailId
    public String getLastedDetailId() {
        // 找到最新的 detailId
        Optional<RestockDetailsBean> latestDetail = restockDetailsRepository.findTopByOrderByDetailIdDesc();

        String newDetailId = "RD001"; // 預設的初始值

        if (latestDetail.isPresent()) {
            String latestId = latestDetail.get().getDetailId(); // 獲取目前最大的 detailId

            // 提取 ID 中的數字部分，去掉前面的 "RD"
            int numericPart = Integer.parseInt(latestId.substring(2));

            // 數字部分加 1
            numericPart++;

            // 格式化為三位數字，並加上 "RD" 前綴
            newDetailId = String.format("RD%03d", numericPart);
        }

        return newDetailId;
    }

    //拿到所有進貨id資訊
    public Page<RestockDTO> getAllRestocks(Pageable pageable) {
        return restocksRepository.findAllRestocks(pageable);
    }

    //透過進貨id拿到所有進貨明細
    public List<RestockDetailDTO>findRestockDetailByRestockId(@RequestParam String restockId){
        return restockDetailsRepository.findRestockDetailByRestockId(restockId);
    }

    // 更新進貨明細並更新進貨總金額
    @Transactional
    public void updateRestockDetailAndTotalPrice(RestockDetailDTO restockDetailDTO) {
        // 1. 更新進貨明細
        restockDetailsRepository.updateRestockNumberAndPrice(
                restockDetailDTO.getNumberOfRestock(),
                restockDetailDTO.getRestockTotalPrice(),
                restockDetailDTO.getPriceAtRestock(),
                restockDetailDTO.getDetailId()
        );

        // 2. 更新 SupplierProduct 的價格
        RestockDetailsBean detail = restockDetailsRepository.findById(restockDetailDTO.getDetailId())
                .orElseThrow(() -> new EntityNotFoundException("RestockDetail not found"));

        // 3. 更新總金額
        updateRestockTotalPrice(restockDetailDTO.getRestockId());

        // 4. 更新進貨商總應付金額
        String supplierId = detail.getSupplier().getSupplierId();
        updateSupplierTotalAmount(supplierId);
        // 5. 更新進貨商未付款總金額
        updateSupplierUnpaidAmount(supplierId);

    }

    // 刪除明細並更新總金額
    public void deleteRestockDetailAndUpdateTotalPrice(String detailId) {
        // 1. 查找進貨明細
        RestockDetailsBean restockDetail = restockDetailsRepository.getById(detailId);
        String restockId = restockDetail.getRestock().getRestockId();
        String supplierId = restockDetail.getSupplier().getSupplierId(); // 在刪除之前先保存 supplierId

        // 2. 刪除進貨明細
        restockDetailsRepository.deleteById(detailId);

        // 3. 更新進貨總金額
        updateRestockTotalPrice(restockId);
        //4.更新進貨商總應付金額
        updateSupplierTotalAmount(supplierId);
        // 5. 更新進貨商未付款總金額
        updateSupplierUnpaidAmount(supplierId);

    }





    // 根據進貨編號更新總金額
    private void updateRestockTotalPrice(String restockId) {
        // 1. 查找所有該進貨編號的明細，計算新的總金額
        List<RestockDetailsBean> details = restockDetailsRepository.findByRestock_RestockId(restockId);
        int newTotalPrice = 0;
        for (RestockDetailsBean detail : details) {
            newTotalPrice += detail.getRestockTotalPrice();
        }
        // 2. 更新進貨表的總金額
        restocksRepository.updateRestocksTotalPrice(restockId, newTotalPrice);

    }


     //根據 供應商id將進貨明細中的restockTotalPrice 加入供應商供應商的totalAmount
    private void updateSupplierTotalAmount(String supplierId){
        List<RestockDetailsBean> details = restockDetailsRepository.findByRestock_SupplierId(supplierId);
        int newTotalAmount = 0;
        for(RestockDetailsBean list :details){
            newTotalAmount += list.getRestockTotalPrice();
        }
        accountsRepository.updateSupplierTotalAmount(supplierId,newTotalAmount);

    }
    //根據 供應商id找到供應商供應商的totalAmount  減去paidAmount 計算新的 unpaidAmount
    private void updateSupplierUnpaidAmount(String supplierId){
        int totalAmount=  supplierAccountsRepository.getTotalAmountBySupplierId(supplierId);
        int paidAmount =  supplierAccountsRepository.getPaidAmountBySupplierId(supplierId);
        int newUnpaidAmount = totalAmount - paidAmount;
        supplierAccountsRepository.updateSupplierUnpaidAmount(supplierId,newUnpaidAmount);
    }







}


