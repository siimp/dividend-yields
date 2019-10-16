package ee.siimp.dividendyields.stockinfo.dto;

import lombok.Value;

@Value
public class StockAndIsinDto {

    Long id;

    String segment;

    String isin;
}
