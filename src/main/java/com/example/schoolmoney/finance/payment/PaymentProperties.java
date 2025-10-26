package com.example.schoolmoney.finance.payment;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.finances.payment")
public class PaymentProperties {

    private String successPath;

    private String failedPath;

}
