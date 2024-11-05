package marketMaster.controller.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import marketMaster.DTO.product.InventoryCheckDetailDTO;
import marketMaster.annotation.RequiresPermission;
import marketMaster.bean.product.InventoryCheckBean;
import marketMaster.bean.product.InventoryCheckDetailsBean;
import marketMaster.bean.product.ProductBean;
import marketMaster.service.product.InventoryCheckDetailsService;
import marketMaster.service.product.InventoryCheckService;
import marketMaster.service.product.ProductService;
import marketMaster.viewModel.EmployeeViewModel;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class InventoryCheckDetailsController {
	
	@Autowired
	private InventoryCheckDetailsService inventoryCheckDetailsService;
	
	@Autowired
	private InventoryCheckService inventoryCheckService;
	
	@Autowired
	private ProductService productService;
	
	@ResponseBody
	@GetMapping("/inventoryCheckDetails/findOne")
	public InventoryCheckDetailsBean findOneInventoryCheckDetail(@RequestParam String detailId) {
		InventoryCheckDetailsBean aInventoryCheckDetail = inventoryCheckDetailsService.findOneInventoryCheckDetailById(detailId);
		return aInventoryCheckDetail;
	}
	
	@ResponseBody
	@GetMapping("/inventoryCheckDetails/findByCheck/Json")
	public List<InventoryCheckDetailsBean> findByInventoryCheckJson(@RequestParam String inventoryCheckId) {
		List<InventoryCheckDetailsBean> detailList = inventoryCheckDetailsService.findByInventoryCheckDetailId(inventoryCheckId);
		return detailList;
	}
	
	@GetMapping("/inventoryCheckDetails/findByCheckId")
	public String findByInventoryCheck(@RequestParam String inventoryCheckId,Model m) {
		List<InventoryCheckDetailsBean> inventoryCheckDetails = inventoryCheckDetailsService.findByInventoryCheckDetailId(inventoryCheckId);
		InventoryCheckBean inventoryCheck = inventoryCheckService.findDetailsBycheckId(inventoryCheckId);
		m.addAttribute("inventoryCheckDetails", inventoryCheckDetails);
		m.addAttribute("inventoryCheck", inventoryCheck);
		return "/product/inventoryChecksDetailsPage";
	}
	
	@PostMapping("/inventoryCheckDetails/deleteByDetailId")
	@RequiresPermission(value = "deleteInventoryCheckDetail",resource = "employee")
	public String deleteDetailById(@RequestBody InventoryCheckDetailDTO inventoryCheckDetailDTO) {
		String detailId = inventoryCheckDetailDTO.getDetailId();
		InventoryCheckDetailsBean detail = inventoryCheckDetailsService.findOneInventoryCheckDetailById(detailId);
		
		inventoryCheckDetailsService.deleteDetailById(detailId);
		
		return "redirect:/inventoryCheckDetails/findByCheckId";
	}
	
	@PostMapping("/inventoryCheckDetails/updateByDetailId")
	public ResponseEntity<Void> updateByDetailId(@RequestBody InventoryCheckDetailDTO inventoryCheckDetailDTO) {
		Integer actualInventory = inventoryCheckDetailDTO.getActualInventory();
		String detailId = inventoryCheckDetailDTO.getDetailId();
		String remark = inventoryCheckDetailDTO.getRemark();
		InventoryCheckDetailsBean detail = inventoryCheckDetailsService.findOneInventoryCheckDetailById(detailId);
		String productId = detail.getProduct().getProductId();
		inventoryCheckDetailsService.updateDetailById(detailId, actualInventory,remark);
		
		return ResponseEntity.ok().build();
	}
	
	@ResponseBody
	@PostMapping("/inventoryCheckDetails/isNewestDetail")
	public boolean isNewestDetail(@RequestBody InventoryCheckDetailDTO inventoryCheckDetailDTO,Model m) {
		String productId = inventoryCheckDetailDTO.getProductId();
		String detailId = inventoryCheckDetailDTO.getDetailId();
		boolean isNewest = inventoryCheckDetailsService.isNewestDetailId(productId, detailId);
		
		m.addAttribute("isNewest", isNewest);
		return isNewest;
	}
	
	
	
	
	
}
