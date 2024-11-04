package marketMaster.controller.bonus;

import marketMaster.DTO.bonus.ItemMgnDTO;
import marketMaster.bean.bonus.ItemManagementBean;
import marketMaster.bean.product.ProductBean;
import marketMaster.exception.BonusException;
import marketMaster.service.bonus.ItemManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // 注意这里需要导入 org.springframework.ui.Model
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Controller
@RequestMapping("/itemManagement")
@CrossOrigin
public class ItemManagementController {
    private static final Logger logger = Logger.getLogger(ItemManagementController.class.getName());
    private final ItemManagementService itemManagementService;

    @Autowired
    public ItemManagementController(ItemManagementService itemManagementService) {
        this.itemManagementService = itemManagementService;
    }

    // 返回视图，添加 Model 参数，并将数据添加到模型中
    @GetMapping
    public String showItemManagementPage(Model model) {
        logger.info("访问商品管理页面");

        // 获取商品列表
        List<ItemMgnDTO> items = itemManagementService.findAllItemsWithPhotos();

        // 获取商品类别列表
        List<String> categories = itemManagementService.getAllAvailableCategories();

        // 将数据添加到模型中
        model.addAttribute("items", items);
        model.addAttribute("categories", categories);

        return "bonus/ItemManagement";
    }

    // 获取可用商品列表
    @GetMapping("/api/products")
    @ResponseBody
    public ResponseEntity<?> getAvailableProducts(
            @RequestParam(required = false) String category) {
        try {
            List<ProductBean> products;
            if (category != null && !category.isEmpty()) {
                products = itemManagementService.getAvailableProductsByCategory(category);
            } else {
                products = itemManagementService.getAvailableProducts();
            }
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            logger.severe("获取可用商品列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 获取商品类别
    @GetMapping("/api/categories")
    @ResponseBody
    public ResponseEntity<?> getCategories() {
        try {
            List<String> categories = itemManagementService.getAllAvailableCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            logger.severe("获取商品类别失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 获取所有商品列表
    @GetMapping("/api/list")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllItems() {
        try {
            List<ItemMgnDTO> items = itemManagementService.findAllItemsWithPhotos();
            Map<String, Object> response = new HashMap<>();
            response.put("data", items); // DataTable要求的格式
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("获取商品数据失败: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "获取商品列表失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    // 获取商品详情
    @GetMapping("/api/item/{itemId}")
    @ResponseBody
    public ResponseEntity<?> getItem(@PathVariable String itemId) {
        try {
            ItemManagementBean item = itemManagementService.findById(itemId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", item
            ));
        } catch (BonusException.InvalidItemDataException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            logger.severe("获取商品详情失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        }
    }

    // 新增商品
    @PostMapping("/api/add")
    @ResponseBody
    public ResponseEntity<?> addItem(@RequestBody ItemManagementBean item) {
        try {
            ItemMgnDTO savedItem = itemManagementService.addItem(item);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "商品新增成功",
                    "item", savedItem
            ));
        } catch (BonusException.DuplicateItemException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        } catch (BonusException.InvalidItemDataException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
             logger.severe("新增商品失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "新增失败: " + e.getMessage()
                    ));
        }
    }

    // 更新商品
    @PutMapping("/api/update/{itemId}")
    @ResponseBody
    public ResponseEntity<?> updateItem(
            @PathVariable String itemId,
            @RequestBody ItemManagementBean item) {
        try {
            if (!itemId.equals(item.getItemId())) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "商品ID不匹配"
                        ));
            }

            ItemMgnDTO updatedItem = itemManagementService.updateItem(itemId, item);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "商品更新成功",
                    "item", updatedItem
            ));
        } catch (BonusException.InvalidItemDataException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            logger.severe("更新商品失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "更新失败: " + e.getMessage()
                    ));
        }
    }

    // 删除商品
    @DeleteMapping("/api/delete/{itemId}")
    @ResponseBody
    public ResponseEntity<?> deleteItem(@PathVariable String itemId) {
        try {
            itemManagementService.deleteItem(itemId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "商品删除成功"
            ));
        } catch (BonusException.InvalidItemDataException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            logger.severe("删除商品失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "删除失败: " + e.getMessage()
                    ));
        }
    }

    // 切换商品状态
    @PostMapping("/api/toggleStatus/{itemId}")
    @ResponseBody
    public ResponseEntity<?> toggleItemStatus(@PathVariable String itemId) {
        try {
            boolean result = itemManagementService.toggleStatus(itemId);
            if (result) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "商品状态已更新"
                ));
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "找不到商品"
                        ));
            }
        } catch (BonusException.InvalidItemDataException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            logger.severe("更新商品状态失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        }
    }
}
