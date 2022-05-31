package ee.siimp.dividendyields;

import ee.siimp.dividendyields.dividend.DividendService;
import ee.siimp.dividendyields.stock.StockService;
import ee.siimp.dividendyields.stockprice.StockPriceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class ApplicationBootstrap implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final DividendService dividendService;

    private final StockService stockService;

    private final DividendYieldsProperties dividendYieldsProperties;

    private final StockPriceService stockPriceService;

    @Override
    public void afterPropertiesSet() {
        LOG.info("bootstrapping application");

        if (dividendYieldsProperties.isUpdateStocksOnStartup()) {
            stockService.updateStockInformation();
        }

        if (dividendYieldsProperties.isUpdateDividendsOnStartup()) {
            int currentYear = LocalDate.now().getYear();
            IntStream.rangeClosed(currentYear - 3, currentYear)
                    .forEach(dividendService::updateDividendInformation);
        }

        if (dividendYieldsProperties.isUpdateStockPriceOnStartup()) {
            stockPriceService.collectStockPricesAtExDividend();
            stockPriceService.collectCurrentStockPricesForFutureDividends();
        }

        LOG.info("bootstrapping finished");
    }

}
