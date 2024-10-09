package marketMaster.controller.bonus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import marketMaster.DTO.bonus.BonusExchangeDTO;
import marketMaster.DTO.bonus.ProductExchangeDTO;
import marketMaster.DTO.bonus.CustomerPointsDTO;
import marketMaster.service.bonus.BonusExchangeService;
import marketMaster.exception.DataAccessException;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@RequestMapping("/bonusExchange")
@CrossOrigin
public class BonusExchangeController {

    @Autowired
    private BonusExchangeService bonusExchangeService;
    private static final Logger logger = Logger.getLogger(BonusExchangeController.class.getName());

    @GetMapping
    public String showBonusExchangeInput(HttpSession session) {
    	session.removeAttribute("currentCustomerTel");
        return "bonus/BonusExchangeInput";
    }

    @PostMapping("/queryProducts")
    public String queryExchangeableProducts(@RequestParam String customerTel, Model model, HttpSession session) {
        try {
            List<ProductExchangeDTO> exchangeableProducts = bonusExchangeService.getExchangeableProducts(customerTel);
            CustomerPointsDTO customerPoints = bonusExchangeService.getCustomerPoints(customerTel);
            model.addAttribute("exchangeableProducts", exchangeableProducts);
            model.addAttribute("customerPoints", customerPoints);
            session.setAttribute("currentCustomerTel", customerTel);
            model.addAttribute("customerTel", customerTel);
            return "bonus/BonusExchangeList";
        } catch (DataAccessException e) {
            model.addAttribute("errorMessage", "查詢可兌換商品失敗: " + e.getMessage());
            return "bonus/BonusExchangeInput";
        }
    }

    @PostMapping("/execute")
    public String executeExchange(/*@RequestParam String customerTel,*/
                                  @RequestParam String productId, 
                                  @RequestParam int exchangeQuantity, 
                                  Model model,
                                  HttpSession session) {
    	String customerTel = (String) session.getAttribute("currentCustomerTel");
        if (customerTel == null) {
            return "redirect:/bonusExchange";
        }
        logger.info("Received execute request for customer: " + customerTel);
        try {
            bonusExchangeService.executeExchange(customerTel, productId, exchangeQuantity);
            model.addAttribute("successMessage", "兌換成功！");
        } catch (DataAccessException e) {
            model.addAttribute("errorMessage", "兌換失敗：" + e.getMessage());
        }
        return queryExchangeableProducts(customerTel, model,session);
    }

    @PostMapping("/records")
    public String showExchangeRecords(Model model, HttpSession session) {
    	String customerTel = (String) session.getAttribute("currentCustomerTel");
        if (customerTel == null) {
            return "redirect:/bonusExchange";
        }
        try {
            List<BonusExchangeDTO> exchangeRecords = bonusExchangeService.getExchangeRecords(customerTel);
            CustomerPointsDTO customerPoints = bonusExchangeService.getCustomerPoints(customerTel);
            model.addAttribute("exchangeRecords", exchangeRecords);
            model.addAttribute("customerPoints", customerPoints);
            return "bonus/BonusExchangeRecord";
        } catch (DataAccessException e) {
            model.addAttribute("errorMessage", "獲取兌換記錄失敗: " + e.getMessage());
            return "redirect:/bonusExchange";  
        }
    }


    @PostMapping("/updateExchangeRecord")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateExchangeRecord(
            @RequestParam String exchangeId,
            @RequestParam String productId,
            @RequestParam int usePoints,
            @RequestParam int numberOfExchange) {
        try {
            bonusExchangeService.updateExchangeRecord(exchangeId, productId, usePoints, numberOfExchange);
            return ResponseEntity.ok(Map.of("status", "success", "message", "兌換記錄更新成功！"));
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "更新兌換記錄失敗", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping("/products")
    @ResponseBody
    public ResponseEntity<List<ProductExchangeDTO>> getProductsByCategory(@RequestParam String category) {
        try {
            List<ProductExchangeDTO> products = bonusExchangeService.getProductsByCategory(category);
            return ResponseEntity.ok(products);
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "獲取商品列表失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }

    @GetMapping("/getProductDetails")
    @ResponseBody
    public ResponseEntity<ProductExchangeDTO> getProductDetails(@RequestParam String productId) {
        try {
            ProductExchangeDTO product = bonusExchangeService.getProductById(productId);
            return ResponseEntity.ok(product);
        } catch (DataAccessException e) {
            logger.severe("獲取商品詳細信息失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/deleteExchangeRecords")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteExchange(@RequestParam String exchangeId) {
    	logger.info("Received delete request for exchangeId: " + exchangeId);
        try {
            bonusExchangeService.deleteExchangeRecord(exchangeId);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "兌換記錄刪除成功！\n\n紅利點數已退還\n已轉回庫存商品"
            ));
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "刪除兌換記錄失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "刪除兌換記錄失敗: " + e.getMessage()));
        }
    }
}