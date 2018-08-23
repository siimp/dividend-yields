package ee.siimp.nasdaqbaltic.stockdividend;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockDividendResultDividendDto {

    private LocalDate dividendDate;

    private Double dividendAmount;

    private Double stockPriceAtExDividend;

    private Double stockPriceLatest;
}
