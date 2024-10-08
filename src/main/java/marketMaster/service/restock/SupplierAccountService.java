package marketMaster.service.restock;

import marketMaster.bean.restock.SupplierAccountsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierAccountService {
    @Autowired
    private SupplierAccountsRepository repository;
    public List<SupplierAccountsBean> getAllSupplierAccounts() {
        return repository.findAll();
    }
    public void updateAccount(String supplierId, int restockTotalPrice) {
        SupplierAccountsBean supplierAccount = repository.findBySupplier_SupplierId(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Supplier ID: " + supplierId));

        // 增加供應商總應付金額
        int newTotalAmount = supplierAccount.getTotalAmount() + restockTotalPrice;
        supplierAccount.setTotalAmount(newTotalAmount);

        // 更新未付款金額
        int newUnpaidAmount = supplierAccount.getUnpaidAmount() + restockTotalPrice;
        supplierAccount.setUnpaidAmount(newUnpaidAmount);

        // 保存更新後的供應商帳戶資料
        repository.save(supplierAccount);
    }

    //根據進貨明細金額
}
