package ee.siimp.dividendyields.stock;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class StockService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Validator validator;

    private final StockRepository stockRepository;

    private final NasdaqBalticEquityListScraper nasdaqBalticEquityListScraper;

    public void updateStockInformation() {
        LOG.info("updating stock infromation");
        List<String> existingStockNames = stockRepository.findAll().stream()
                .map(Stock::getTicker)
                .collect(Collectors.toList());

        List<Stock> newStocks = nasdaqBalticEquityListScraper.loadAllStocks().stream()
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
