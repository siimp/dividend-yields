package ee.siimp.nasdaqbaltic.stockprice;

import ee.siimp.nasdaqbaltic.dividend.Dividend;
import ee.siimp.nasdaqbaltic.dividend.DividendRepository;
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

    public void collectStockPricesAtExDividend() {
        LOG.info("collecting stock prices without dividend");
        List<Dividend> dividendsWithoutStockPriceInfo = dividendRepository.findDividendsWithoutStockPriceInfo();
        LOG.info("{} dividends are without stock price info", dividendsWithoutStockPriceInfo.size());
        for (Dividend dividend : dividendsWithoutStockPriceInfo) {

        }

    }
}
