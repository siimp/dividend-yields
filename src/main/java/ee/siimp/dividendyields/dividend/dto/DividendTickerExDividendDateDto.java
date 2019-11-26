package ee.siimp.dividendyields.dividend.dto;

import lombok.Value;

import java.time.LocalDate;


public interface DividendTickerExDividendDateDto {
    String getStockTicker();
    LocalDate getExDividendDate();
    boolean isCapitalDecrease();
}
