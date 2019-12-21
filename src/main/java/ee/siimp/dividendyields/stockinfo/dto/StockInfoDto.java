package ee.siimp.dividendyields.stockinfo.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

@Data
@Builder
public class StockInfoDto {
    private BigInteger numberOfSecurities;
}
