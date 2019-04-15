package ee.siimp.nasdaqbaltic.stockinfo;

import ee.siimp.nasdaqbaltic.common.entity.BaseEntity;
import ee.siimp.nasdaqbaltic.stock.Stock;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"stock_id", "numberOfSecurities"}, name = "uc_stock_info_stock_id_number_of_securities"))
@Getter
@Setter
public class StockInfo extends BaseEntity {

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false)
    private Stock stock;

    @NotNull
    @Min(0)
    private BigInteger numberOfSecurities;
}
