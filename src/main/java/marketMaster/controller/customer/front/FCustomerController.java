package marketMaster.controller.customer.front;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import marketMaster.annotation.RequiresPermission;
import marketMaster.bean.customer.CustomerBean;
import marketMaster.service.authorization.AuthorizationService;
import marketMaster.service.customer.CustomerRepository;
import marketMaster.service.customer.CustomerServiceImpl;
import marketMaster.viewModel.EmployeeViewModel;

@Controller
@RequestMapping("/customer/front")
public class FCustomerController {

	@Autowired
	private CustomerServiceImpl customerService;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private AuthorizationService authService;
	
	@GetMapping("/cusList")
	@RequiresPermission(value = "viewList", resource = "customer")
	public String getAllCustomer(@RequestParam(required = false) String searchTel,
								@RequestParam(defaultValue = "0") int page,
								@RequestParam(defaultValue = "10") int size,
								Model model, HttpSession session) {
		// 獲取當前登入用戶
	    EmployeeViewModel currentEmployee = (EmployeeViewModel) session.getAttribute("frontendEmployee");
	    int authority = currentEmployee.getAuthority();  // 獲取權限等級
		
		Page<CustomerBean> customerPage = customerService.searchCustomers(searchTel, PageRequest.of(page, size));
		
		model.addAttribute("customers", customerPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", customerPage.getTotalPages());
		model.addAttribute("totalItems", customerPage.getTotalElements());
		model.addAttribute("searchTel", searchTel);
		model.addAttribute("currentAuthority", authority);  // 將權限等級傳遞給視圖
		
		return "customer/front/FrontCustomerList";
	}
	
	@GetMapping("/details")
	@RequiresPermission(value = "view", resource = "customer")
	public String showCustomerDetails(@RequestParam String customerTel, Model model) {
		Optional<CustomerBean> customerOptional = customerService.getCustomer(customerTel);
		model.addAttribute("customer", customerOptional.get());
		return "customer/front/FrontCustomerDetails";
	}
	
    @GetMapping("/delete")
    @RequiresPermission(value = "delete", resource = "customer")
    public String deleteCustomer(@RequestParam String customerTel, RedirectAttributes redirectAttributes) {
        boolean deleted = customerService.deleteCustomer(customerTel);
        if (deleted) {
            redirectAttributes.addFlashAttribute("message", "會員已成功刪除");
        }
        return "redirect:/front/cusList";
    }
    
	@GetMapping("/cusAddForm")
	@RequiresPermission(value = "add", resource = "customer")
	public String showAddForm() {
		return "customer/front/FrontAddCustomer";
	}
	
	@PostMapping("/cusAdd")
	@RequiresPermission(value = "add", resource = "customer")
	public String addCustomer(@ModelAttribute CustomerBean customer, RedirectAttributes redirectAttributes, Model model) {
        customer.setDateOfRegistration(LocalDate.now());
        customer.setTotalPoints(0);
        
        String result = customerService.addCustomer(customer);
        
        if (result.equals("success")) {
            redirectAttributes.addFlashAttribute("message", "會員新增成功");
            return "redirect:/customer/front/cusList";
		} else {
	        model.addAttribute("error", result);
	        model.addAttribute("customer", customer);
	        return "customer/front/FrontAddCustomer";
	    }
	}
	
	@GetMapping("/getUpdate")
	@RequiresPermission(value = "update", resource = "customer")
	public String showUpdateForm(@RequestParam String customerTel, Model model) {
		Optional<CustomerBean> customerOptional = customerService.getCustomer(customerTel);
		
		if (customerOptional.isPresent()) {
			model.addAttribute("customer", customerOptional.get());
			model.addAttribute("originalTel", customerTel);
			return "customer/front/FrontUpdateCustomer";
		} else {
			return "redirect:/customer/front/cusList";
		}
	}
	
	@PostMapping("/cusUpdate")
	@RequiresPermission(value = "update", resource = "customer")
	public String updateCustomer(@ModelAttribute CustomerBean customer,
	                             @RequestParam String originalTel,
	                             RedirectAttributes redirectAttributes,
	                             Model model) {
	    Optional<CustomerBean> existingCustomerOpt = customerService.getCustomer(originalTel);
	    if (existingCustomerOpt.isPresent()) {
	        CustomerBean existingCustomer = existingCustomerOpt.get();
	        // 保留原有的註冊日期和累積紅利點數
	        customer.setDateOfRegistration(existingCustomer.getDateOfRegistration());
	        customer.setTotalPoints(existingCustomer.getTotalPoints());
	        
	        boolean success = customerService.updateCustomer(customer, originalTel);
	        if (success) {
	            redirectAttributes.addFlashAttribute("message", "會員資料更新成功");
	            return "redirect:/customer/front/cusList";
	        } else {
	            model.addAttribute("error", "會員資料更新失敗");
	        }
	    } else {
	        model.addAttribute("error", "找不到該會員");
	    }

	    // 更新失敗時，保留用戶輸入的數據
	    model.addAttribute("customer", customer);
	    model.addAttribute("originalTel", originalTel);
	    return "customer/front/FrontUpdateCustomer";
	}
	
	// 結帳時驗證是否有會員電話號碼
	@GetMapping("/validate")
	@ResponseBody
	public Map<String, Boolean> validateCustomerTel(@RequestParam String tel) {
	    return Map.of("exists", customerRepository.existsById(tel));
	}
    
}
