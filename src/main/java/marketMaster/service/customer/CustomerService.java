package marketMaster.service.customer;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import marketMaster.bean.customer.CustomerBean;

@Service
@Transactional
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	public Page<CustomerBean> getAllCustomersSortedByDate(Pageable pageable) {
		return customerRepository.findAllByOrderByDateOfRegistrationDesc(pageable);
	}

	public Optional<CustomerBean> getCustomer(String customerTel) {
		return customerRepository.findById(customerTel);
	}

	public boolean deleteCustomer(String customerTel) {
		customerRepository.deleteById(customerTel);
		return true;
	}

	public String addCustomer(CustomerBean customer) {
		
	    if (customerRepository.existsById(customer.getCustomerTel())) {
	        return "該手機號碼已存在，請再確認";
	    }
	    
		customerRepository.save(customer);
		return "success";
	}
	
    public Page<CustomerBean> searchCustomers(String customerTel, Pageable pageable) {
        if (customerTel != null && !customerTel.isEmpty()) {
            return customerRepository.findByCustomerTelContaining(customerTel, pageable);
        } else {
            return customerRepository.findAllByOrderByDateOfRegistrationDesc(pageable);
        }
    }

	public boolean updateCustomer(CustomerBean customer, String originalTel) {
		
		if (!originalTel.equals(customer.getCustomerTel())) {
			// 如果手機號碼被更改，檢查新號碼是否已存在
			if (customerRepository.existsById(customer.getCustomerTel())) {
				return false; // 新號碼已存在，更新失敗
			}
			// 刪除舊記錄
			customerRepository.deleteById(originalTel);
		}
		// 保存新記錄
		customerRepository.save(customer);
		
		return true;
	}
	
}
