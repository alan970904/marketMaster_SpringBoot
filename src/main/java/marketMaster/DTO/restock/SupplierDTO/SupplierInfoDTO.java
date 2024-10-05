package marketMaster.DTO.restock.SupplierDTO;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SupplierInfoDTO {

        private String supplierId;
        private String supplierName;
        private String address;
        private String contactPerson;
        private String phone;
        private String email;
        private int totalAmount;
        private int paidAmount;
        private int unpaidAmount;

}
