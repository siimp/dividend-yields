package ee.siimp.dividendyields;

import ee.siimp.dividendyields.dividend.DividendService;
import ee.siimp.dividendyields.stock.StockService;
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
public class NasdaqbalticApplicationBootstrap implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final DividendService dividendService;

    private final StockService stockService;

    private final NasdaqBalticProperties nasdaqBalticProperties;

    @Override
    public void afterPropertiesSet() {
        LOG.info("bootstrapping application");

        if (nasdaqBalticProperties.isUpdateStocksOnStartup()) {
            stockService.updateStockInformation();
        }

        if (nasdaqBalticProperties.isUpdateDividendsOnStartup()) {
            int currentYear = LocalDate.now().getYear();
            IntStream.rangeClosed(currentYear - 3, currentYear)
                    .forEach(dividendService::updateDividendInformation);
        }

        LOG.info("bootstrapping finished");
    }

}
