package ee.siimp.nasdaqbaltic.stockinfo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Component
@Validated
@ConfigurationProperties(prefix = "stock-info")
@Getter
@Setter
class StockInfoProperties {

    @NotBlank
    private String updateJobCron;

    @NotBlank
    private String endpoint;
}
