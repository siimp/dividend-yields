package ee.siimp.nasdaqbaltic.dividend;

import ee.siimp.nasdaqbaltic.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"symbol", "exDividendDate"}, name = "uc_dividend_symbol_ex_dividend_date"))
@Getter
@Setter
public class Dividend extends BaseEntity {

    @NotBlank
    private String symbol;

    @NotNull
    private LocalDate exDividendDate;

    @NotNull
    private Double amount;

}
