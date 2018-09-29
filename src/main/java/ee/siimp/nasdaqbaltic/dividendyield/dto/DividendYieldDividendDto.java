package ee.siimp.nasdaqbaltic.dividendyield.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class DividendYieldDividendDto {

    private LocalDate exDividendDate;
    private BigDecimal dividendAmount;
    private BigDecimal stockPriceAtExDividend;
    private BigDecimal dividendYield;
    private boolean capitalDecrease;
}
