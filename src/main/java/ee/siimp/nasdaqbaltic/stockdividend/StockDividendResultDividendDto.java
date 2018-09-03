package ee.siimp.nasdaqbaltic.stockdividend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class StockDividendResultDividendDto {

    private LocalDate exDividendDate;

    private BigDecimal amount;

}
