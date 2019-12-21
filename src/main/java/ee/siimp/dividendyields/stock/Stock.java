package ee.siimp.dividendyields.stock;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import ee.siimp.dividendyields.common.entity.BaseEntity;

import ee.siimp.dividendyields.dividend.Dividend;
import ee.siimp.dividendyields.stock.dto.StockDto;
import ee.siimp.dividendyields.stockinfo.StockInfo;
import ee.siimp.dividendyields.stockprice.StockPrice;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"ticker"}, name = "uc_stock_ticker"))
@Getter
@Setter
@ToString(exclude = {"dividends", "prices"})
public class Stock extends BaseEntity {

    public static final String SEGMENT_MAIN_LIST = "Baltic Main List";
    public static final String SEGMENT_SECONDARY_LIST = "Baltic Secondary List";
    public static final String SEGMENT_FIRST_NORTH_LIST = "First North Baltic Share List";

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

    @OneToMany(mappedBy = "stock")
    private List<StockPrice> prices;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id")
    private StockInfo stockInfo;

    static Stock of(StockDto dto) {
        Stock stock = new Stock();
        stock.setName(dto.getName());
        stock.setIsin(dto.getIsin());
        stock.setCurrency(dto.getCurrency());
        stock.setTicker(dto.getTicker());
        stock.setMarketPlace(dto.getMarketPlace());
        stock.setSegment(dto.getSegment());
        return stock;
    }
}
