package ee.siimp.nasdaqbaltic.stockprice;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
@AllArgsConstructor
class StockPriceJob {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private StockPriceService stockPriceService;

    @Scheduled(cron = "0 0 6 * * *")
    void execute() {
        LOG.info("executing collectStockPricesAtExDividend");
        stockPriceService.collectStockPricesAtExDividend();
        LOG.info("done");

        LOG.info("executing collectCurrentStockPricesForFutureDividends");
        stockPriceService.collectCurrentStockPricesForFutureDividends();
        LOG.info("done");
    }
}
