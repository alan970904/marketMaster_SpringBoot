package marketMaster.service.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import marketMaster.bean.customer.CustomerBean;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerBean, String> {

	Page<CustomerBean> findAllByOrderByDateOfRegistrationDesc(Pageable pageable);
	
	Page<CustomerBean> findByCustomerTelContaining(String customerTel, Pageable pageable);
	
}
