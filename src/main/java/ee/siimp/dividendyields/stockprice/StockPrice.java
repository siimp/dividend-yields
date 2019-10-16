package ee.siimp.dividendyields.stockprice;

import ee.siimp.dividendyields.common.entity.BaseEntity;
import ee.siimp.dividendyields.stock.Stock;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"stock_id", "date"}, name = "uc_stock_price_stock_id_date"))
@Getter
@Setter
public class StockPrice extends BaseEntity {

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false)
    private Stock stock;

    @NotNull
    private LocalDate date;

    @NotNull
    @Min(0)
    @Column(precision = 7, scale = 4)
    private BigDecimal price;
}
