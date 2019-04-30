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
    private BigDecimal totalDividendYield = BigDecimal.ZERO;
    private BigDecimal totalYesterdaysDividendYield = BigDecimal.ZERO;
    private List<DividendYieldDividendDto> dividends = new ArrayList<>();

    public static DividendYieldResultDto of(DividendYieldDto dto) {
        DividendYieldResultDto result = new DividendYieldResultDto();
        result.setName(dto.getName());
        result.setTicker(dto.getTicker());
        result.setIsin(dto.getIsin());
        addDividend(dto, result);

        return result;
    }

    public void add(DividendYieldDto dto) {
        addDividend(dto, this);
    }

    private static void addDividend(DividendYieldDto dto, DividendYieldResultDto result) {
        result.setTotalDividendYield(result.getTotalDividendYield().add(dto.getDividendYield()));

        DividendYieldDividendDto yieldResult = new DividendYieldDividendDto();
        yieldResult.setDividendAmount(dto.getDividendAmount());
        yieldResult.setDividendYield(dto.getDividendYield());
        yieldResult.setExDividendDate(dto.getExDividendDate());
        yieldResult.setStockPriceAtExDividend(dto.getStockPriceAtExDividend());
        yieldResult.setCurrentStockPrice(dto.getCurrentStockPrice());
        yieldResult.setDividendCost(dto.getDividendCost());
        if (dto.getYesterdaysDividendYield() != null) {
           result.setTotalYesterdaysDividendYield(result.getTotalYesterdaysDividendYield()
                   .add(dto.getYesterdaysDividendYield()));
        }

        yieldResult.setCapitalDecrease(dto.isCapitalDecrease());
        result.getDividends().add(yieldResult);
    }


}
