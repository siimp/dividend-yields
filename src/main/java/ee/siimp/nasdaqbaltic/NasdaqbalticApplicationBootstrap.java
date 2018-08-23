package ee.siimp.nasdaqbaltic;

import ee.siimp.nasdaqbaltic.dividend.DividendService;
import ee.siimp.nasdaqbaltic.stock.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class NasdaqbalticApplicationBootstrap implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private DividendService dividendService;

    @Autowired
    private StockService stockService;

    @Value("${nasdaqbaltic.load-initial-data}")
    private Boolean loadInitialData;

    @Override
    public void afterPropertiesSet() {
        LOG.info("bootstraping application");

        if (Boolean.TRUE.equals(loadInitialData)) {
            stockService.updateStockInformation();
            dividendService.updateDividendInformation();
        }

        LOG.info("bootstraping finished");
    }

}
