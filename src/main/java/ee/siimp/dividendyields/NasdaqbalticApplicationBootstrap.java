package ee.siimp.dividendyields;

import java.lang.invoke.MethodHandles;

import ee.siimp.dividendyields.dividend.DividendService;
import ee.siimp.dividendyields.stock.StockService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NasdaqbalticApplicationBootstrap implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final DividendService dividendService;

    private final StockService stockService;

    private final NasdaqBalticProperties nasdaqBalticProperties;

    @Override
    public void afterPropertiesSet() {
        LOG.info("bootstraping application");

        if (Boolean.TRUE.equals(nasdaqBalticProperties.isLoadInitialData())) {
            stockService.updateStockInformation();
            dividendService.updateDividendInformation();
        }

        LOG.info("bootstraping finished");
    }

}
