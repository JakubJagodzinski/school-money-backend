package com.example.schoolmoney.finance;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.finances")
public class FinanceConfiguration {

    private String currency;

}
