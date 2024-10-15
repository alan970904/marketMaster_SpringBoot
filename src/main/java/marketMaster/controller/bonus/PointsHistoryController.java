package marketMaster.controller.bonus;

import marketMaster.DTO.bonus.PointsHistoryDTO;
import marketMaster.service.bonus.PointsHistoryService;
import marketMaster.service.bonus.EncryptionService;
import marketMaster.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.logging.Logger;
import java.util.logging.Level;

@Controller
@RequestMapping("/pointsHistory")
public class PointsHistoryController {
	private static final Logger logger = Logger.getLogger(PointsHistoryController.class.getName());
	
	@Autowired
	private PointsHistoryService pointsHistoryService;
	
	@Autowired
	private EncryptionService encryptionService;


	@GetMapping("/members")
    public String listMembersWithPointsHistory(Model model) {
        try {
            List<PointsHistoryDTO> allHistories = pointsHistoryService.getAllPointsHistories();
            Map<String, Map<String, Object>> memberTotalPoints = allHistories.stream()
                .collect(Collectors.groupingBy(PointsHistoryDTO::getCustomerTel,
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            int totalPoints = list.stream().mapToInt(PointsHistoryDTO::getPointsChange).sum();
                            PointsHistoryDTO first = list.get(0);
                            return Map.of(
                                "totalPoints", totalPoints,
                                "encryptedCustomerId", encryptionService.encrypt(first.getCustomerTel()),
                                "customerName", first.getCustomerName()
                            );
                        }
                    )
                ));
            model.addAttribute("memberTotalPoints", memberTotalPoints);
            return "bonus/PointsHistoryList";
        } catch (DataAccessException e) {
            model.addAttribute("error", "獲取會員點數記錄失敗: " + e.getMessage());
            return "error";
        }
    }
    
	@GetMapping("/detail")
    
	public String getMemberPointsHistory(@RequestParam("id") String encryptedId, Model model) {
		 
        String decryptedId = encryptionService.decrypt(encryptedId);
        return showMemberPointsHistory(decryptedId, model);
    }

	 @GetMapping("/search")
    public String showSearchForm() {
		logger.info("Accessing search form");
        return "bonus/PointsHistorySearch";
    }

	 @GetMapping("/searchResult")
	 public String searchMembers(@RequestParam String customerName, Model model) {
	     try {
	    	 logger.info("Searching for customer with name: " + customerName);
	         List<PointsHistoryDTO> searchResults = pointsHistoryService.searchPointsHistoriesByCustomerName(customerName);
	         logger.info("Found " + searchResults.size() + " results");
	         model.addAttribute("searchResults", searchResults);
	         return "bonus/PointsHistorySearchResult";
	     } catch (DataAccessException e) {
	    	 logger.log(Level.SEVERE, "Error searching for customers", e);
	         model.addAttribute("error", "搜索會員失敗: " + e.getMessage());
	         return "error";
	     } catch (Exception e) {
	    	 logger.log(Level.SEVERE, "Unexpected error during customer search", e);
	         model.addAttribute("error", "發生意外錯誤，請稍後再試");
	         return "error";
	     }
	 }
    
	 private String showMemberPointsHistory(String customerTel, Model model) {
	        try {
	            List<PointsHistoryDTO> histories = pointsHistoryService.getPointsHistoriesByCustomerTel(customerTel);
	            int totalPoints = histories.stream().mapToInt(PointsHistoryDTO::getPointsChange).sum();
	            model.addAttribute("histories", histories);
	            model.addAttribute("encryptedCustomerId", encryptionService.encrypt(customerTel));
	            model.addAttribute("totalPoints", totalPoints);
	            return "bonus/PointsHistoryDetail";
	        } catch (DataAccessException e) {
	            model.addAttribute("error", "獲取會員點數紀錄失敗: " + e.getMessage());
	            return "error";
	        }
	    }
    
    @GetMapping("/api")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')") // 假設使用 Spring Security
    public ResponseEntity<List<PointsHistoryDTO>> getAllPointsHistories() {
        try {
            List<PointsHistoryDTO> pointsHistories = pointsHistoryService.getAllPointsHistories();
            return ResponseEntity.ok(pointsHistories);
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PointsHistoryDTO> getPointsHistoryById(@PathVariable Integer id) {
        try {
            PointsHistoryDTO pointsHistory = pointsHistoryService.getPointsHistoryById(id);
            return ResponseEntity.ok(pointsHistory);
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/api/customer/{encryptedCustomerTel}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PointsHistoryDTO>> getPointsHistoriesByCustomerTel(@PathVariable String encryptedCustomerTel) {
        try {
            String customerTel = encryptionService.decrypt(encryptedCustomerTel);
            List<PointsHistoryDTO> pointsHistories = pointsHistoryService.getPointsHistoriesByCustomerTel(customerTel);
            return ResponseEntity.ok(pointsHistories);
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/api")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PointsHistoryDTO> createPointsHistory(@RequestBody PointsHistoryDTO pointsHistory) {
        try {
            PointsHistoryDTO createdPointsHistory = pointsHistoryService.createPointsHistory(pointsHistory);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPointsHistory);
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PointsHistoryDTO> updatePointsHistory(@PathVariable Integer id, @RequestBody PointsHistoryDTO pointsHistory) {
        try {
            PointsHistoryDTO updatedPointsHistory = pointsHistoryService.updatePointsHistory(id, pointsHistory);
            return ResponseEntity.ok(updatedPointsHistory);
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePointsHistory(@PathVariable Integer id) {
        try {
            pointsHistoryService.deletePointsHistory(id);
            return ResponseEntity.noContent().build();
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @ExceptionHandler({Exception.class, RuntimeException.class})
    public String handleAllExceptions(Exception e, Model model) {
        if (e instanceof RuntimeException) {
            logger.severe("Runtime exception occurred: " + e.getMessage());
            model.addAttribute("error", "處理請求時發生錯誤，請稍後再試。");
        } else {
            logger.severe("Unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "發生錯誤，請稍後再試或聯絡系統管理員。");
        }
        return "error"; // 確保你有一個 error.html 模板
    }
//    @ExceptionHandler(Exception.class)
//    public String handleException(Exception e, Model model) {
//        logger.severe("Unexpected error occurred: " + e.getMessage());
//        e.printStackTrace();
//        model.addAttribute("error", "發生錯誤，請稍後再試或聯絡系統管理員。");
//        return "error";  // 確保你有一個 error.html 模板
//    }
//    @ExceptionHandler(RuntimeException.class)
//    public String handleRuntimeException(RuntimeException e, Model model) {
//        logger.severe("Runtime exception occurred: " + e.getMessage());
//        model.addAttribute("error", "處理請求時發生錯誤，請稍後再試。");
//        return "error";
//    }
}