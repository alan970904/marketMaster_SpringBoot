package marketMaster.service.customer;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import marketMaster.bean.customer.CustomerBean;

public interface CustomerService {

	Page<CustomerBean> getAllCustomersSortedByDate(Pageable pageable);

	Optional<CustomerBean> getCustomer(String customerTel);

	boolean deleteCustomer(String customerTel);

	String addCustomer(CustomerBean customer);

	Page<CustomerBean> searchCustomers(String customerTel, Pageable pageable);

	boolean updateCustomer(CustomerBean customer, String originalTel);

}
