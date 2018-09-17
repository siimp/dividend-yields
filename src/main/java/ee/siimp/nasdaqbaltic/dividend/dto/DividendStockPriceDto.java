package ee.siimp.nasdaqbaltic.dividend.dto;

import java.time.LocalDate;

public interface DividendStockPriceDto {

    Long getStockId();

    String getStockIsin();

    LocalDate getExDividendDate();
}
