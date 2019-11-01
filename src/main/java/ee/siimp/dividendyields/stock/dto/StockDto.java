package ee.siimp.dividendyields.stock.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockDto {

    private String name;
    private String isin;
    private String currency;
    private String ticker;
    private String marketPlace;
    private String segment;

}
