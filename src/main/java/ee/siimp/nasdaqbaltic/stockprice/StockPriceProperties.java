package ee.siimp.nasdaqbaltic.stockprice;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Component
@Validated
@ConfigurationProperties(prefix = "stockprice")
@Getter
@Setter
public class StockPriceProperties {

    @NotBlank
    private String updateJobCron;

    @NotBlank
    private String endpoint;

}
