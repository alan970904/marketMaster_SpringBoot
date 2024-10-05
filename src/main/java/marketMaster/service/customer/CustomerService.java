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
	
}
