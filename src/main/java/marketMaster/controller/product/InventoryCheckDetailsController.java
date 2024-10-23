package marketMaster.controller.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import marketMaster.DTO.product.InventoryCheckDetailDTO;
import marketMaster.bean.product.InventoryCheckDetailsBean;
import marketMaster.bean.product.ProductBean;
import marketMaster.service.product.InventoryCheckDetailsService;
import marketMaster.service.product.ProductService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class InventoryCheckDetailsController {
	
	@Autowired
	private InventoryCheckDetailsService inventoryCheckDetailsService;
	
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
		m.addAttribute("inventoryCheckDetails", inventoryCheckDetails);
		return "/product/inventoryChecksDetailsPage";
	}
	
	@PostMapping("/inventoryCheckDetails/deleteByDetailId")
	public String deleteDetailById(@RequestBody InventoryCheckDetailDTO inventoryCheckDetailDTO) {
		String detailId = inventoryCheckDetailDTO.getDetailId();
		InventoryCheckDetailsBean detail = inventoryCheckDetailsService.findOneInventoryCheckDetailById(detailId);
		Integer currentInventory = detail.getCurrentInventory();
		String productId = detail.getProduct().getProductId();
		
		productService.updateProductByDeleteCheck(productId, currentInventory);
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
		productService.updateProductByUpdateCheck(productId, actualInventory);
		inventoryCheckDetailsService.updateDetailById(detailId, actualInventory,remark);
		
		return ResponseEntity.ok().build();
	}
	
	
	@PostMapping("/inventoryCheckDetails/isNewestDetail")
	public ResponseEntity<Void> isNewestDetail(@RequestParam String productId,@RequestParam String detailId,Model m) {
		boolean isNewest = inventoryCheckDetailsService.findNewestDetailId(productId, detailId);
		
		m.addAttribute("isNewest", isNewest);
		return ResponseEntity.ok().build();
	}
	
	
}
