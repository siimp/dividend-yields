package ee.siimp.dividendyields.dividendyield.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DividendYieldDividendDto {

    private LocalDate exDividendDate;
    private BigDecimal dividendAmount;
    private BigDecimal stockPriceAtExDividend;
    private BigDecimal currentStockPrice;
    private BigDecimal dividendYield;
    private BigDecimal dividendCost;
    private boolean capitalDecrease;
}
