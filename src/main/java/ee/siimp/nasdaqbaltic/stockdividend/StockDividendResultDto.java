package ee.siimp.nasdaqbaltic.stockdividend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class StockDividendResultDto {

    private String name;
    private String ticker;
    private List<StockDividendResultDividendDto> dividends;

}
