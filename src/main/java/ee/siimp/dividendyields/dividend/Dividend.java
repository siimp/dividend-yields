package ee.siimp.dividendyields.dividend;

import ee.siimp.dividendyields.common.entity.BaseEntity;
import ee.siimp.dividendyields.stock.Stock;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"stock_id", "exDividendDate", "capitalDecrease"},
        name = "uc_dividend_stock_id_ex_dividend_date_capital_decrease"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dividend extends BaseEntity {

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false)
    private Stock stock;

    @NotNull
    private LocalDate exDividendDate;

    @NotNull
    @Min(0)
    @Column(precision = 7, scale = 5)
    private BigDecimal amount;

    @NotNull
    @Builder.Default
    private boolean capitalDecrease = false;

}
