package ee.siimp.dividendyields.dividend;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import ee.siimp.dividendyields.common.entity.BaseEntity;
import ee.siimp.dividendyields.stock.Stock;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"stock_id", "exDividendDate"}, name = "uc_dividend_stock_id_ex_dividend_date"))
@Getter
@Setter
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
    private boolean capitalDecrease = false;

}
