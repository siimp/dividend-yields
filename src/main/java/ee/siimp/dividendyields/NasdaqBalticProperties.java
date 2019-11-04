package ee.siimp.dividendyields;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@ConfigurationProperties(prefix = "nasdaqbaltic")
@Getter
@Setter
public class NasdaqBalticProperties {

    private boolean updateStocksOnStartup;

    private boolean updateDividendsOnStartup;

}
