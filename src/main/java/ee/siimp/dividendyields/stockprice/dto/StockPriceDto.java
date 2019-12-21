package ee.siimp.dividendyields.stockprice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;


@Data
@Builder
public class StockPriceDto {
    private BigDecimal average;
}
