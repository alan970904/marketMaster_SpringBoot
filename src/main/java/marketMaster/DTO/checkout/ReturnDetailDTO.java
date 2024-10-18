package marketMaster.DTO.checkout;

import lombok.Data;

@Data
public class ReturnDetailDTO {
    private String productId;
    private String productName;
    private Integer numberOfReturn;
    private Integer productPrice;
    private String reasonForReturn;
    private Integer returnPrice;
    private String returnStatus;
    private String returnPhoto;
}