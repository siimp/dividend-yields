package ee.siimp.dividendyields.dividendyield.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DividendYieldDto {

    String getName();

    String getTicker();

    String getIsin();

    LocalDate getExDividendDate();

    BigDecimal getDividendAmount();

    BigDecimal getDividendCost();

    BigDecimal getStockPriceAtExDividend();

    BigDecimal getCurrentStockPrice();

    BigDecimal getDividendYield();

    BigDecimal getYesterdaysDividendYield();

    boolean isCapitalDecrease();

}
