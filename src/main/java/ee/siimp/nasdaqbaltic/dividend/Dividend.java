package ee.siimp.nasdaqbaltic.dividend;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import ee.siimp.nasdaqbaltic.common.entity.BaseEntity;
import ee.siimp.nasdaqbaltic.stock.Stock;

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
    private Double amount;

    @NotBlank
    @NotNull
    private String currency;

}
