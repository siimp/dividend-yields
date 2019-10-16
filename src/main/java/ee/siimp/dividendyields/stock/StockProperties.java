package ee.siimp.dividendyields.stock;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Component
@Validated
@ConfigurationProperties(prefix = "stock")
@Getter
@Setter
public class StockProperties {

    @NotBlank
    private String endpoint;

    private Resource staticList;
}
