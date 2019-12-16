package ee.siimp.dividendyields.stockprice;

import ee.siimp.dividendyields.common.utils.ThreadUtils;
import ee.siimp.dividendyields.dividend.DividendRepository;
import ee.siimp.dividendyields.dividend.dto.DividendStockPriceDto;
import ee.siimp.dividendyields.stock.Stock;
import ee.siimp.dividendyields.stockprice.dto.StockPriceDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
@RequiredArgsConstructor
class StockPriceService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final DividendRepository dividendRepository;

    private final StockPriceRepository stockPriceRepository;

    private final EntityManager entityManager;

    private final NasdaqBalticStockPriceScraper nasdaqBalticStockPriceScraper;

    void collectStockPricesAtExDividend() {
        LOG.info("collecting stock prices for past dividends");
        List<DividendStockPriceDto> pastDividendsWithoutStockPriceInfo =
                dividendRepository.findPastDividendsWithoutStockPriceInfo();
        LOG.info("{} past dividends are without stock price info", pastDividendsWithoutStockPriceInfo.size());
        for (DividendStockPriceDto dto : pastDividendsWithoutStockPriceInfo) {
            StockPriceDto stockPriceDto = nasdaqBalticStockPriceScraper
                    .loadStockPrice(dto.getStockIsin(), dto.getExDividendDate());

            if (stockPriceDto != null) {
                saveStockPrice(dto, stockPriceDto);
            }

            ThreadUtils.randomSleep();
        }
    }

    private void saveStockPrice(DividendStockPriceDto dto, StockPriceDto stockPriceDto) {
        LOG.info("saving new stock price {}", stockPriceDto);
        StockPrice stockPrice = StockPrice.builder()
                .stock(entityManager.getReference(Stock.class, dto.getStockId()))
                .date(dto.getExDividendDate())
                .price(stockPriceDto.getAverage())
                .build();
        LOG.debug("saving new stock price {}", stockPrice);
        stockPriceRepository.save(stockPrice);
    }

    public void collectCurrentStockPricesForFutureDividends() {
        LOG.warn("collectCurrentStockPricesForFutureDividends unimplemented!!!");
    }
}
