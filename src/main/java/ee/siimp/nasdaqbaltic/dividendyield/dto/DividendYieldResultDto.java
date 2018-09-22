package ee.siimp.nasdaqbaltic.dividendyield.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DividendYieldResultDto {

    String getName();

    String getTicker();

    String getIsin();

    LocalDate getExDividendDate();

    BigDecimal getDividendAmount();

    BigDecimal getStockPriceAtExDividend();

    BigDecimal getDividendYield();


}
