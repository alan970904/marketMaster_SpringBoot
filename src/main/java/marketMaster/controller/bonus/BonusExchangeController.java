package marketMaster.controller.bonus;

import marketMaster.DTO.bonus.BonusExchangeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import marketMaster.DTO.bonus.CustomerPointsDTO;
import marketMaster.DTO.bonus.ItemMgnDTO;
import marketMaster.bean.bonus.BonusExchangeBean;
import marketMaster.service.bonus.BonusExchangeService;
import marketMaster.exception.DataAccessException;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Controller
@RequestMapping("/bonusExchange")
@CrossOrigin
public class BonusExchangeController {
    private static final Logger logger = Logger.getLogger(BonusExchangeController.class.getName());
    private final BonusExchangeService bonusExchangeService;

    // 新增鎖對象用於 同步控制
    private static final Object exchangeLock = new Object();

    @Autowired
    public BonusExchangeController(BonusExchangeService bonusExchangeService) {
        this.bonusExchangeService = bonusExchangeService;
    }

    // 基本頁面導向
    @GetMapping
    public String showBonusExchangeInput(HttpSession session) {
        logger.info("進入紅利兌換輸入頁面");
        session.removeAttribute("currentCustomerTel");
        return "bonus/BonusExchangeInput";
    }

    // 根據會員查詢可兌換商品
    @PostMapping("/queryProducts")
    public String queryExchangeableProducts(
            @RequestParam String customerTel,
            Model model,
            HttpSession session) {
        logger.info("開始查詢會員可兌換商品，會員電話: " + customerTel);
        try {
            // 加強輸入驗證
            if (customerTel == null || !customerTel.matches("^[0-9]{10}$")) {
                throw new DataAccessException("請輸入有效的手機號碼(10碼)");
            }

            List<ItemMgnDTO> exchangeableProducts = bonusExchangeService.getExchangeableProducts(customerTel);
            CustomerPointsDTO customerPoints = bonusExchangeService.getCustomerPoints(customerTel);

            if (exchangeableProducts.isEmpty()) {
                logger.info("未找到可兌換商品");
                model.addAttribute("warningMessage", "目前沒有可兌換的商品");
            }

            model.addAttribute("exchangeableProducts", exchangeableProducts);
            model.addAttribute("customerPoints", customerPoints);
            session.setAttribute("currentCustomerTel", customerTel);
            model.addAttribute("customerTel", customerTel);

            return "bonus/BonusExchangeList";
        } catch (DataAccessException e) {
            logger.warning("查詢失敗: " + e.getMessage());
            model.addAttribute("errorMessage", "查詢可兌換商品失敗: " + e.getMessage());
            return "bonus/BonusExchangeInput";
        }
    }

    // 執行兌換 - 加入同步控制
    @PostMapping("/execute")
    @ResponseBody
    public ResponseEntity<Map<String, String>> executeExchange(
            @RequestParam String itemId,
            @RequestParam int exchangeQuantity,
            HttpSession session) {

        String customerTel = (String) session.getAttribute("currentCustomerTel");
        if (customerTel == null) {
            logger.warning("Session已過期");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "Session已過期，請重新登入"));
        }

        // 驗證兌換數量
        if (exchangeQuantity <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "兌換數量必須大於0"));
        }

        synchronized (exchangeLock) {
            try {
                // 驗證商品是否仍然可用
                ItemMgnDTO item = bonusExchangeService.validateExchangeItem(itemId);
                if (item == null) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("status", "error", "message", "商品不存在或已下架"));
                }

                // 驗證庫存
                if (item.getItemMaximum() < exchangeQuantity) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("status", "error",
                                    "message", "庫存不足，當前可兌換數量: " + item.getItemMaximum()));
                }

                BonusExchangeBean exchange = new BonusExchangeBean();
                exchange.setCustomerTel(customerTel);
                exchange.setItemId(itemId);
                exchange.setNumberOfExchange(exchangeQuantity);

                bonusExchangeService.save(exchange);

                return ResponseEntity.ok()
                        .body(Map.of("status", "success",
                                "message", "兌換成功"));
            } catch (DataAccessException e) {
                logger.warning("兌換失敗: " + e.getMessage());
                return ResponseEntity.badRequest()
                        .body(Map.of("status", "error",
                                "message", e.getMessage()));
            }
        }
    }

    // 取得可兌換商品列表
    @GetMapping("/getExchangeableItems")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getExchangeableItems(@RequestParam String customerTel) {
        try {
            List<ItemMgnDTO> items = bonusExchangeService.getExchangeableProducts(customerTel);
            CustomerPointsDTO points = bonusExchangeService.getCustomerPoints(customerTel);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", items,
                    "customerPoints", points
            ));
        } catch (DataAccessException e) {
            logger.warning("獲取可兌換商品失敗: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    // 查詢會員點數
    @GetMapping("/getCustomerPoints")
    @ResponseBody
    public ResponseEntity<CustomerPointsDTO> getCustomerPoints(@RequestParam String customerTel) {
        try {
            CustomerPointsDTO points = bonusExchangeService.getCustomerPoints(customerTel);
            return ResponseEntity.ok(points);
        } catch (DataAccessException e) {
            logger.warning("獲取會員點數失敗: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    // 展示所有兌換記錄
    @GetMapping("/findAllExchangeList")
    public String findAll(Model model) {
        try {
            List<BonusExchangeBean> bonusExchangeList = bonusExchangeService.findAllBonusExchange();
            model.addAttribute("bonusExchangeList", bonusExchangeList);
            return "bonus/BonusExchangeList";
        } catch (DataAccessException e) {
            logger.warning("獲取所有兌換記錄失敗: " + e.getMessage());
            model.addAttribute("errorMessage", "獲取兌換記錄失敗: " + e.getMessage());
            return "error/generalError";
        }
    }

    // 顯示兌換記錄頁面
    @PostMapping("/records")
    public String showExchangeRecords(
            @RequestParam String customerTel,
            Model model, HttpServletResponse response) {
        logger.info("查看會員兌換記錄: " + customerTel);
        try {
            // 設置響應頭以防止分塊傳輸
            response.setHeader("Transfer-Encoding", "identity");
            CustomerPointsDTO customerPoints = bonusExchangeService.getCustomerPoints(customerTel);
            List<BonusExchangeDTO> records = bonusExchangeService.getExchangeRecordsByCustomer(customerTel);

            model.addAttribute("customerPoints", customerPoints);
            model.addAttribute("customerTel", customerTel);
            model.addAttribute("records", records);

            return "bonus/BonusExchangeRecord";
        } catch (DataAccessException e) {
            logger.warning("獲取兌換記錄失敗: " + e.getMessage());
            model.addAttribute("errorMessage", "獲取兌換記錄失敗: " + e.getMessage());
            return "redirect:/bonusExchange";
        }
    }

    // 獲取兌換記錄數據
    @GetMapping("/getRecords")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getExchangeRecords
    (@RequestParam String customerTel,
    HttpServletResponse response) {
        try {
            // 設置響應頭
            response.setHeader("Transfer-Encoding", "identity");
            response.setContentType("application/json;charset=UTF-8");
            logger.info("獲取會員兌換記錄: " + customerTel);
            List<BonusExchangeDTO> records = bonusExchangeService.getExchangeRecordsByCustomer(customerTel);
            return ResponseEntity.ok(Map.of("data", records));
        } catch (DataAccessException e) {
            logger.warning("獲取兌換記錄失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 刪除兌換記錄 - 加入同步控制
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteExchange(@RequestParam String exchangeId) {
        logger.info("開始刪除兌換記錄: " + exchangeId);

        synchronized (exchangeLock) {
            try {
                if (exchangeId == null || exchangeId.trim().isEmpty()) {
                    throw new DataAccessException("兌換記錄ID不能為空");
                }
                bonusExchangeService.deleteById(exchangeId);
                logger.info("兌換記錄刪除成功");
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "兌換記錄刪除成功！\n紅利點數已退還，商品已轉回庫存"
                ));
            } catch (DataAccessException e) {
                logger.warning("刪除兌換記錄失敗: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "status", "error",
                                "message", "刪除失敗: " + e.getMessage()
                        ));
            }
        }
    }

    // 更新兌換記錄 - 加入同步控制
    @PutMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateBonusExchange(
            @RequestParam String exchangeId,
            @RequestParam String itemId,
            @RequestParam String customerTel,
            @RequestParam int numberOfExchange) {

        synchronized (exchangeLock) {
            try {
                logger.info("開始更新兌換記錄: " + exchangeId);

                // 驗證兌換數量
                if (numberOfExchange <= 0) {
                    throw new DataAccessException("兌換數量必須大於0");
                }

                BonusExchangeBean exchange = new BonusExchangeBean();
                exchange.setExchangeId(exchangeId);
                exchange.setItemId(itemId);
                exchange.setCustomerTel(customerTel);
                exchange.setNumberOfExchange(numberOfExchange);

                bonusExchangeService.updateBonusExchange(exchange);

                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "兌換記錄更新成功"
                ));
            } catch (DataAccessException e) {
                logger.warning("更新失敗: " + e.getMessage());
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "status", "error",
                                "message", e.getMessage()
                        ));
            }
        }
    }
}