package ee.siimp.nasdaqbaltic.stockprice;

import ee.siimp.nasdaqbaltic.common.service.NasdaqBalticStockPriceService;
import ee.siimp.nasdaqbaltic.dividend.DividendRepository;
import ee.siimp.nasdaqbaltic.dividend.dto.DividendStockPriceDto;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
@AllArgsConstructor
public class StockPriceService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private DividendRepository dividendRepository;

    private NasdaqBalticStockPriceService nasdaqBalticStockPriceService;

    public void collectStockPricesAtExDividend() {
        LOG.info("collecting stock prices without dividend");
        List<DividendStockPriceDto> dividendsWithoutStockPriceInfo = dividendRepository.findDividendsWithoutStockPriceInfo();
        LOG.info("{} dividends are without stock price info", dividendsWithoutStockPriceInfo.size());
        for (DividendStockPriceDto dto : dividendsWithoutStockPriceInfo) {
            nasdaqBalticStockPriceService.loadStockPrice(dto.getStockId(), dto.getStockIsin(), dto.getExDividendDate());

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
