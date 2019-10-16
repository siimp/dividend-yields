package ee.siimp.dividendyields.stockprice;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
@RequiredArgsConstructor
class StockPriceJob {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final StockPriceService stockPriceService;

    @Scheduled(cron = "#{stockPriceProperties.getUpdateJobCron()}")
    void execute() {
        LOG.info("executing collectStockPricesAtExDividend");
        stockPriceService.collectStockPricesAtExDividend();
        LOG.info("done");

        LOG.info("executing collectCurrentStockPricesForFutureDividends");
        stockPriceService.collectCurrentStockPricesForFutureDividends();
        LOG.info("done");
    }
}
