package marketMaster.controller.bonus;

import marketMaster.DTO.bonus.PointsHistoryDTO;
import marketMaster.service.bonus.PointsHistoryService;
import marketMaster.service.bonus.EncryptionService;
import marketMaster.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pointsHistory")
public class PointsHistoryController {

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

    @PostMapping("/search")
    public String pointsHistorySearch(@RequestParam String customerTel, Model model) {
        return "redirect:/pointsHistory/detail?id=" + customerTel;
    }
    
    private String showMemberPointsHistory(String customerTel, Model model) {
        try {
            List<PointsHistoryDTO> histories = pointsHistoryService.getPointsHistoriesByCustomerTel(customerTel);
            int totalPoints = histories.stream().mapToInt(PointsHistoryDTO::getPointsChange).sum();
            model.addAttribute("histories", histories);
            model.addAttribute("customerTel", customerTel);
            model.addAttribute("totalPoints", totalPoints);
            return "bonus/PointsHistoryDetail";
        } catch (DataAccessException e) {
            model.addAttribute("error", "獲取會員點數紀錄失敗: " + e.getMessage());
            return "error";
        }
    }
    

    @GetMapping("/api")
    @ResponseBody
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
    public ResponseEntity<PointsHistoryDTO> getPointsHistoryById(@PathVariable Integer id) {
        try {
            PointsHistoryDTO pointsHistory = pointsHistoryService.getPointsHistoryById(id);
            return ResponseEntity.ok(pointsHistory);
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/api/customer/{customerTel}")
    @ResponseBody
    public ResponseEntity<List<PointsHistoryDTO>> getPointsHistoriesByCustomerTel(@PathVariable String customerTel) {
        try {
            List<PointsHistoryDTO> pointsHistories = pointsHistoryService.getPointsHistoriesByCustomerTel(customerTel);
            return ResponseEntity.ok(pointsHistories);
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/api")
    @ResponseBody
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
    public ResponseEntity<Void> deletePointsHistory(@PathVariable Integer id) {
        try {
            pointsHistoryService.deletePointsHistory(id);
            return ResponseEntity.noContent().build();
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}