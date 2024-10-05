package marketMaster.controller.customer;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import marketMaster.bean.customer.CustomerBean;
import marketMaster.service.customer.CustomerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
	private CustomerService customerService;
	
	@GetMapping("/cusList")
	public String getAllCustomer(@RequestParam(required = false) String searchTel,
								@RequestParam(defaultValue = "0") int page,
								@RequestParam(defaultValue = "10") int size,
								Model model) {
		Page<CustomerBean> customerPage = customerService.searchCustomers(searchTel, PageRequest.of(page, size));
		
		model.addAttribute("customers", customerPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", customerPage.getTotalPages());
		model.addAttribute("totalItems", customerPage.getTotalElements());
		model.addAttribute("searchTel", searchTel);
		
		return "customer/CustomerList";
	}
	
	@GetMapping("/details")
	public String showCustomerDetails(@RequestParam String customerTel, Model model) {
		Optional<CustomerBean> customerOptional = customerService.getCustomer(customerTel);
		model.addAttribute("customer", customerOptional.get());
		return "customer/CustomerDetails";
	}
	
    @GetMapping("/delete")
    public String deleteCustomer(@RequestParam String customerTel, RedirectAttributes redirectAttributes) {
        boolean deleted = customerService.deleteCustomer(customerTel);
        if (deleted) {
            redirectAttributes.addFlashAttribute("message", "會員已成功刪除");
        }
        return "redirect:/customer/cusList";
    }
    
	@GetMapping("/cusAddForm")
	public String showAddForm() {
		return "customer/AddCustomer";
	}
	
	@PostMapping("/cusAdd")
	public String addCustomer(@ModelAttribute CustomerBean customer, RedirectAttributes redirectAttributes, Model model) {
        customer.setDateOfRegistration(LocalDate.now());
        customer.setTotalPoints(0);
        
        String result = customerService.addCustomer(customer);
        
        if (result.equals("success")) {
            redirectAttributes.addFlashAttribute("message", "會員新增成功");
            return "redirect:/customer/cusList";
		} else {
	        model.addAttribute("error", result);
	        model.addAttribute("customer", customer);
	        return "customer/AddCustomer";
	    }
	}
}
