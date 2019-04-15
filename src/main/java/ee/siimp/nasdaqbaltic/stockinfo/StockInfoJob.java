package ee.siimp.nasdaqbaltic.stockinfo;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
@RequiredArgsConstructor
class StockInfoJob {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final StockInfoService stockInfoService;

    @Scheduled(cron = "#{stockPriceProperties.getUpdateJobCron()}")
    void execute() {
        LOG.info("executing collectStockInfo");
        stockInfoService.collectStockInfo();
        LOG.info("done");
    }
}
