package ee.siimp.nasdaqbaltic.dividendyield.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DividendYieldResultDto {

    private String name;
    private String ticker;
    private String isin;
    private BigDecimal totalDividendYield;
    private List<DividendYieldDividendDto> dividends = new ArrayList<>();

    public static DividendYieldResultDto of(DividendYieldRepositoryDto dto) {
        DividendYieldResultDto result = new DividendYieldResultDto();
        result.setName(dto.getName());
        result.setTicker(dto.getTicker());
        result.setIsin(dto.getIsin());
        result.setTotalDividendYield(dto.getDividendYield());

        addDividend(dto, result);

        return result;
    }

    public void add(DividendYieldRepositoryDto dto) {
        addDividend(dto, this);
    }

    private static void addDividend(DividendYieldRepositoryDto dto, DividendYieldResultDto result) {
        result.setTotalDividendYield(result.getTotalDividendYield().add(dto.getDividendYield()));

        DividendYieldDividendDto yieldResult = new DividendYieldDividendDto();
        yieldResult.setDividendAmount(dto.getDividendAmount());
        yieldResult.setDividendYield(dto.getDividendYield());
        yieldResult.setExDividendDate(dto.getExDividendDate());
        yieldResult.setStockPriceAtExDividend(dto.getStockPriceAtExDividend());
        yieldResult.setCapitalDecrease(dto.isCapitalDecrease());
        result.getDividends().add(yieldResult);
    }


}
