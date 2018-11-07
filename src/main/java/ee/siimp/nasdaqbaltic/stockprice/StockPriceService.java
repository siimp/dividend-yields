package ee.siimp.nasdaqbaltic.stockprice;

import ee.siimp.nasdaqbaltic.common.service.NasdaqBalticStockPriceService;
import ee.siimp.nasdaqbaltic.dividend.DividendRepository;
import ee.siimp.nasdaqbaltic.dividend.dto.DividendStockPriceDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockPriceService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final DividendRepository dividendRepository;

    private final NasdaqBalticStockPriceService nasdaqBalticStockPriceService;

    void collectStockPricesAtExDividend() {
        LOG.info("collecting stock prices without dividend");
        List<DividendStockPriceDto> pastDividendsWithoutStockPriceInfo =
                dividendRepository.findDividendsWithoutStockPriceInfo()
                        .stream()
                        .filter(it -> LocalDate.now().isAfter(it.getExDividendDate()))
                        .collect(Collectors.toList());
        LOG.info("{} dividends are without stock price info", pastDividendsWithoutStockPriceInfo.size());
        for (DividendStockPriceDto dto : pastDividendsWithoutStockPriceInfo) {
            nasdaqBalticStockPriceService.loadStockPrice(dto.getStockId(), dto.getStockIsin(), dto.getExDividendDate());

            sleep();
        }
    }

    void collectCurrentStockPricesForFutureDividends() {
        LocalDate now = LocalDate.now();
        LOG.info("collecting todays stock prices for future dividends");
        List<DividendStockPriceDto> futureDividendsWithoutStockPriceInfo =
                dividendRepository.findDividendsWithoutStockPriceInfo()
                        .stream()
                        .filter(it -> now.isBefore(it.getExDividendDate()) || now.isEqual(it.getExDividendDate()))
                        .collect(Collectors.toList());
        LOG.info("{} future dividends are without todays stock price info", futureDividendsWithoutStockPriceInfo.size());
        for (DividendStockPriceDto dto : futureDividendsWithoutStockPriceInfo) {
            nasdaqBalticStockPriceService.loadStockPrice(dto.getStockId(), dto.getStockIsin(), now);
            sleep();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage(), e);
        }
    }


}
