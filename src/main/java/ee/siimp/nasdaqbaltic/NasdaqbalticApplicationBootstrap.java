package ee.siimp.nasdaqbaltic;

import ee.siimp.nasdaqbaltic.common.NasdaqBalticDividendService;
import ee.siimp.nasdaqbaltic.common.NasdaqBalticStockService;
import ee.siimp.nasdaqbaltic.stock.Stock;
import ee.siimp.nasdaqbaltic.stock.StockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class NasdaqbalticApplicationBootstrap implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final int DIVIDEND_STARTING_YEAR = 2015;

    @Autowired
    private NasdaqBalticStockService nasdaqBalticStockService;

    @Autowired
    private NasdaqBalticDividendService nasdaqBalticDividendService;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private Validator validator;

    @Override
    public void afterPropertiesSet() throws Exception {
        LOG.info("bootstraping application");
        updateStockInformation();
        updateDividendInfromation();
    }

    private void updateDividendInfromation() {
        for (int year = DIVIDEND_STARTING_YEAR; year <= LocalDate.now().getYear(); year++) {
            try {
                nasdaqBalticDividendService.loadYearDividends(year);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                // just continue
            }
        }
    }

    private void updateStockInformation() throws IOException {
        LOG.info("updating stock infromation");
        List<String> existingStockNames = stockRepository.findAll().stream()
                .map(Stock::getTicker)
                .collect(Collectors.toList());

        List<Stock> newStocks = nasdaqBalticStockService.loadAllStocks().stream()
                .filter(it -> !existingStockNames.contains(it.getTicker()))
                .collect(Collectors.toList());

        for (Stock stock : newStocks) {
            Set<ConstraintViolation<Stock>> errors = validator.validate(stock);
            if (CollectionUtils.isEmpty(errors)) {
                LOG.debug("adding new stock {}", stock);
                stockRepository.save(stock);
            } else {
                LOG.warn("stock {} validation failed {}", stock, errors);
            }
        }
    }
}
