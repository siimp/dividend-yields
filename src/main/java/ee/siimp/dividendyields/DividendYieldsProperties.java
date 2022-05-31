package ee.siimp.dividendyields;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@ConfigurationProperties(prefix = "dividend-yields")
@Getter
@Setter
public class DividendYieldsProperties {

    private boolean updateStocksOnStartup;

    private boolean updateDividendsOnStartup;

    private boolean updateStockPriceOnStartup;

}
