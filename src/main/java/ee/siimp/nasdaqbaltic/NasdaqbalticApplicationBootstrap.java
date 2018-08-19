package ee.siimp.nasdaqbaltic;

import ee.siimp.nasdaqbaltic.common.NasdaqBalticService;
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
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class NasdaqbalticApplicationBootstrap implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private NasdaqBalticService nasdaqBalticService;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private Validator validator;

    @Override
    public void afterPropertiesSet() throws Exception {

        List<String> existingStockNames = stockRepository.findAll().stream()
                .map(Stock::getTicker)
                .collect(Collectors.toList());

        List<Stock> newStocks = nasdaqBalticService.loadAllStocks().stream()
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
