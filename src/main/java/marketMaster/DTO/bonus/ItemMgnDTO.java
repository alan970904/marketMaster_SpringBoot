package marketMaster.DTO.bonus;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
//exchangeable_products
//可兌換商品(管理）
//ProductExchangeDTO改為ItemMgnDTO提升可讀性
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemMgnDTO {
	private String itemId;
	private String productId; // 新增
	private String productName;
	private String productCategory;
	private int productPrice;       // 新增
	private int itemPoints;
	private int itemMaximum;
	private LocalDate startDate;
	private LocalDate endDate;
	private boolean active;
	private byte[] productPhoto;
}