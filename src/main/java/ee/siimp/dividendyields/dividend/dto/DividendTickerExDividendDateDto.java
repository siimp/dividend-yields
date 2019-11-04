package ee.siimp.dividendyields.dividend.dto;

import lombok.Value;

import java.time.LocalDate;


public interface DividendTickerExDividendDateDto {
    String getTicker();
    LocalDate getExDividendDate();
    boolean isCapitalDecrease();
}
