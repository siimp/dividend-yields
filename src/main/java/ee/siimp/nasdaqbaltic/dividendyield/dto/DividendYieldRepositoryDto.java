package ee.siimp.nasdaqbaltic.dividendyield.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DividendYieldRepositoryDto {

    String getName();

    String getTicker();

    String getIsin();

    LocalDate getExDividendDate();

    BigDecimal getDividendAmount();

    BigDecimal getStockPriceAtExDividend();

    BigDecimal getCurrentStockPrice();

    BigDecimal getYesterdayStockPrice();

    BigDecimal getDividendYield();

    boolean isCapitalDecrease();


}
