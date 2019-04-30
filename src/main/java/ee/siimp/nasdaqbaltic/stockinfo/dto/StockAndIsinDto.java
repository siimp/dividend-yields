package ee.siimp.nasdaqbaltic.stockinfo.dto;

import lombok.Value;

@Value
public class StockAndIsinDto {

    Long id;

    String segment;

    String isin;
}
