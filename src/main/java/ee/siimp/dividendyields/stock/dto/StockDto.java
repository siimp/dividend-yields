package ee.siimp.dividendyields.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
