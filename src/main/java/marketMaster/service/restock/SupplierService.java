package marketMaster.service.restock;

import marketMaster.DTO.restock.SupplierDTO.SupplierIdAndNameDTO;
import marketMaster.DTO.restock.SupplierDTO.SupplierInfoDTO;
import marketMaster.bean.restock.SupplierAccountsBean;
import marketMaster.bean.restock.SuppliersBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class SupplierService {

    @Autowired
    private SuppliersRepository suppliersRepository;
    //找出所有供應商
    public List<SuppliersBean> getAllSuppliers() {
        return suppliersRepository.findAll();
    }

    //透過id找到供應商
    public SuppliersBean findSupplierById(String supplierId) {
        return suppliersRepository.findById(supplierId).orElse(null);
    }
    //插入新的供應商
    public SuppliersBean saveSupplier(SuppliersBean suppliersBean) {
        return suppliersRepository.save(suppliersBean);
    }
    //透過id更新供應商
    @Transactional
    @Modifying
    public SuppliersBean updateSupplier(SuppliersBean suppliersBean) {
       SuppliersBean supplierId= findSupplierById(suppliersBean.getSupplierId());
       if(supplierId!=null){
           supplierId.setSupplierName(suppliersBean.getSupplierName());
           supplierId.setAddress(suppliersBean.getAddress());
           supplierId.setTaxNumber(suppliersBean.getTaxNumber());
           supplierId.setContactPerson(suppliersBean.getContactPerson());
           supplierId.setPhone(suppliersBean.getPhone());
           supplierId.setEmail(suppliersBean.getEmail());
           supplierId.setBankAccount(suppliersBean.getBankAccount());
           return suppliersRepository.save(supplierId);
       }
       return null;
    }

    //透過Id刪除
    @Transactional
    public void deleteSupplier(String supplierId) {
        suppliersRepository.deleteById(supplierId);
    }

    public List<SupplierInfoDTO> getAllSuppliersWithAccounts() {
        return suppliersRepository.findAllSuppliersWithAccounts();
    }

    //找所有supplier Id 跟name
    public List<SupplierIdAndNameDTO>findAllSupplierIdAndName(){
        return suppliersRepository.findAllSupplierIdAndName();
    }




}
