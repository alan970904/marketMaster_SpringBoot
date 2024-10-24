package marketMaster.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Getter
@Configuration
public class ECPayConfig {
    // Getters
    @Value("${ecpay.merchant-id}")
    private String merchantId;

    @Value("${ecpay.hash-key}")
    private String hashKey;

    @Value("${ecpay.hash-iv}")
    private String hashIv;

    @Value("${ecpay.return-url}")
    private String returnUrl;

    @Value("${ecpay.client-back-url}")
    private String clientBackUrl;

    @Value("${ecpay.order-result-url}")
    private String orderResultUrl;

}