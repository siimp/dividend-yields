package ee.siimp.dividendyields.stockprice;

import ee.siimp.dividendyields.common.utils.ThreadUtils;
import ee.siimp.dividendyields.dividend.DividendRepository;
import ee.siimp.dividendyields.dividend.dto.DividendStockPriceDto;
import ee.siimp.dividendyields.stockprice.dto.StockPriceDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
@RequiredArgsConstructor
class StockPriceService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final DividendRepository dividendRepository;

    private StockPriceRepository stockPriceRepository;

    private final NasdaqBalticStockPriceScraper nasdaqBalticStockPriceScraper;

    void collectStockPricesAtExDividend() {
        LOG.info("collecting stock prices for past dividends");
        List<DividendStockPriceDto> pastDividendsWithoutStockPriceInfo =
                dividendRepository.findPastDividendsWithoutStockPriceInfo();
        LOG.info("{} past dividends are without stock price info", pastDividendsWithoutStockPriceInfo.size());
        for (DividendStockPriceDto dto : pastDividendsWithoutStockPriceInfo) {
            StockPriceDto stockPriceDto = nasdaqBalticStockPriceScraper
                    .loadStockPrice(dto.getStockIsin(), dto.getExDividendDate());
            saveStockPrice(dto, stockPriceDto);

            ThreadUtils.randomSleep();
        }
    }

    private void saveStockPrice(DividendStockPriceDto dto, StockPriceDto stockPriceDto) {
        LOG.info("saving new stock price {}", stockPriceDto);
    }

    void collectCurrentStockPricesForFutureDividends() {
        /*
        LocalDate now = LocalDate.now();
        LOG.info("collecting todays stock prices for future dividends");
        List<DividendStockPriceDto> futureDividendsWithoutStockPriceInfo =
                dividendRepository.findFutureDividendsWithoutStockPriceInfo();
        LOG.info("{} future dividends are without todays stock price info", futureDividendsWithoutStockPriceInfo.size());
        for (DividendStockPriceDto dto : futureDividendsWithoutStockPriceInfo) {
            nasdaqBalticStockPriceScraper.loadStockPrice(dto.getStockId(), dto.getStockIsin(), now);

            ThreadUtils.randomSleep();
        }
        */
    }


}
