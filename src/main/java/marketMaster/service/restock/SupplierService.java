package marketMaster.service.restock;

import marketMaster.DTO.restock.SupplierDTO.SupplierIdAndNameDTO;
import marketMaster.DTO.restock.SupplierDTO.SupplierInfoDTO;
import marketMaster.bean.restock.SuppliersBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    //新增 新的供應商
    public void saveSupplier(SuppliersBean suppliersBean) {
        suppliersRepository.save(suppliersBean);
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


    //拿到所有供應商資訊
    public Page<SupplierInfoDTO> getAllSuppliersWithAccounts(Pageable pageable) {
        return suppliersRepository.findAllSuppliersWithAccounts(pageable);
    }

    //找所有supplier Id 跟name
    public List<SupplierIdAndNameDTO>findAllSupplierIdAndName(){
        return suppliersRepository.findAllSupplierIdAndName();
    }
    //拿到最新的供應商Id
    public String getLastSupplierId() {
          String lastSupplierId= suppliersRepository.getLastSupplierId();
          if(lastSupplierId==null){
              return "S001";
          }
          int Id=Integer.parseInt(lastSupplierId.substring(1));
          int nextID = Id+1;
        return String.format("S%03d", nextID);
    }



    //更新供應商詳細資料但是稅務號碼沒改
    @Transactional
    @Modifying
    public String updateSupplierBySupplierId(SupplierInfoDTO supplierInfoDTO) {
         String supplierID = supplierInfoDTO.getSupplierId();
         SuppliersBean supplierBean = findSupplierById(supplierID);
         supplierBean.setSupplierName(supplierInfoDTO.getSupplierName());
         supplierBean.setAddress(supplierInfoDTO.getAddress());
         supplierBean.setContactPerson(supplierInfoDTO.getContactPerson());
         supplierBean.setPhone(supplierInfoDTO.getPhone());
         supplierBean.setEmail(supplierInfoDTO.getEmail());
         suppliersRepository.save(supplierBean);
        return supplierID;
    }

    //拿到所有有關於該供應商所有的有關的訂單細節


}
