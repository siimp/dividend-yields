package ee.siimp.nasdaqbaltic.stock;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import ee.siimp.nasdaqbaltic.common.entity.BaseEntity;

import ee.siimp.nasdaqbaltic.dividend.Dividend;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"ticker"}, name = "uc_stock_ticker"))
@Getter
@Setter
@ToString
public class Stock extends BaseEntity {

    @NotBlank
    @NotNull
    private String name;

    @NotBlank
    @NotNull
    private String isin;

    @NotBlank
    @NotNull
    private String currency;

    @NotBlank
    @NotNull
    private String ticker;

    @NotBlank
    @NotNull
    private String marketPlace;

    @NotBlank
    @NotNull
    private String segment;

    @OneToMany(mappedBy = "stock")
    private List<Dividend> dividends;

}
