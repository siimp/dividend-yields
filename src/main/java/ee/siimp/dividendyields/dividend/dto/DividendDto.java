package ee.siimp.dividendyields.dividend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class DividendDto {

    private String ticker;
    private String issuer;
    private String market;
    private BigDecimal amount;
    private LocalDate exDividendDate;
    private boolean capitalDecrease;
}
