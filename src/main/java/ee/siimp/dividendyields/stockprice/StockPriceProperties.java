package ee.siimp.dividendyields.stockprice;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Component
@Validated
@ConfigurationProperties(prefix = "stock-price")
@Getter
@Setter
class StockPriceProperties {

    @NotBlank
    private String updateJobCron;

    @NotBlank
    private String endpoint;

}
