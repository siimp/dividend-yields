package ee.siimp.nasdaqbaltic.stock;

import ee.siimp.nasdaqbaltic.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"ticker"}, name = "uc_stock_ticker"))
@Getter
@Setter
@ToString
public class Stock extends BaseEntity {

    @NotBlank
    private String name;

    @NotBlank
    private String isin;

    @NotBlank
    private String currency;

    @NotBlank
    private String ticker;

    @NotBlank
    private String marketPlace;

    @NotBlank
    private String segment;

}
