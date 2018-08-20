package ee.siimp.nasdaqbaltic.dividend;

import ee.siimp.nasdaqbaltic.common.BaseEntity;
import ee.siimp.nasdaqbaltic.stock.Stock;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"stock_id", "exDividendDate"}, name = "uc_dividend_stock_id_ex_dividend_date"))
@Getter
@Setter
public class Dividend extends BaseEntity {

    @NotNull
    @ManyToOne
    private Stock stock;

    @NotNull
    private LocalDate exDividendDate;

    @NotNull
    private Double amount;

    @NotBlank
    private String currency;

}
