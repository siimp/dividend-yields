package ee.siimp.dividendyields.dividend.dto;

import java.time.LocalDate;

public interface DividendStockPriceDto {

    Long getStockId();

    String getStockIsin();

    LocalDate getExDividendDate();
}
