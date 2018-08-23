package ee.siimp.nasdaqbaltic.stockdividend;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockDividendResultDto {

    private String stockName;

    private String stockSymbol;

    private List<StockDividendResultDividendDto> dividends;

}
