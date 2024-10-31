package marketMaster.controller.bonus;

import com.fasterxml.jackson.databind.ObjectMapper;
import marketMaster.DTO.bonus.ItemMgnDTO;
import marketMaster.bean.bonus.ItemManagementBean;
import marketMaster.service.bonus.ItemManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

//import java.util.Date;
//import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping("/itemManagement")
@CrossOrigin
public class ItemManagementController {
    private static final Logger logger = Logger.getLogger(ItemManagementController.class.getName());
    private final ItemManagementService itemManagementService;
//    private final ObjectMapper objectMapper;

    @Autowired
    public ItemManagementController(ItemManagementService itemManagementService, ObjectMapper objectMapper) {
        this.itemManagementService = itemManagementService;
//        this.objectMapper = objectMapper;
    }

    // 新增僅返回視圖的方法
    @GetMapping
    public String showItemManagementPage() {
        logger.info("訪問商品管理頁面");
        return "bonus/ItemManagement";
    }

    // 修改 API 端點以支持 DataTable
    @GetMapping("/api/list")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllItems() {
        logger.info("開始獲取商品數據");
        try {
            List<ItemMgnDTO> items = itemManagementService.findAllItemsWithPhotos();
            Map<String, Object> response = new HashMap<>();
            response.put("data", items); // DataTables 需要的格式
            //  logger.info("成功獲取 " + items.size() + " 條商品記錄");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("獲取商品數據失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "獲取商品列表失敗：" + e.getMessage()));
        }
    }
    // 新增切換狀態的端點
    @PostMapping("/api/toggleStatus/{itemId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleItemStatus(@PathVariable String itemId) {
        try {
            boolean result = itemManagementService.toggleStatus(itemId);
            if (result) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "商品狀態已更新"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "找不到商品"
                ));
            }
        } catch (Exception e) {
            logger.severe("更新商品狀態失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 新增獲取單個商品的方法
    @GetMapping("/api/item/{itemId}")
    @ResponseBody
    public ResponseEntity<?> getItem(@PathVariable String itemId) {
        try {
            ItemManagementBean item = itemManagementService.findById(itemId);
            if (item == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "找不到商品"));
            }
            return ResponseEntity.ok(Map.of("success", true, "data", item));
        } catch (Exception e) {
            logger.severe("獲取商品詳情失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    //新增可兌換商品
    @PostMapping("/api/add")
    public ResponseEntity<?> addItem(@RequestBody ItemManagementBean item) {
        try {
            ItemMgnDTO savedItem = itemManagementService.addItem(item);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "商品新增成功",
                    "item", savedItem
            ));
        } catch (Exception e) {
            logger.severe("新增失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false,
                            "message", "新增失敗: " + e.getMessage()));
        }
    }

    //更新可兌換商品
    @PutMapping("/api/update/{itemId}")
    public ResponseEntity<?> updateItem(@PathVariable String itemId,
                                        @RequestBody ItemManagementBean item) {
        try {
            if (!itemId.equals(item.getItemId())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "商品ID不匹配"));
            }

            ItemMgnDTO updatedItem = itemManagementService.updateItem(itemId, item);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "商品更新成功",
                    "item", updatedItem
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "更新失敗: " + e.getMessage()));
        }
    }

    //刪除可兌換商品
    @DeleteMapping("/api/delete/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable String itemId) {
        try {
            itemManagementService.deleteItem(itemId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "商品刪除成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "刪除失敗: " + e.getMessage()));
        }
    }
}