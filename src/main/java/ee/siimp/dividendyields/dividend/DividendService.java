package ee.siimp.dividendyields.dividend;

import ee.siimp.dividendyields.dividend.dto.DividendDto;
import ee.siimp.dividendyields.dividend.dto.DividendTickerExDividendDateDto;
import ee.siimp.dividendyields.stock.Stock;
import ee.siimp.dividendyields.stock.StockRepository;
import ee.siimp.dividendyields.stock.dto.StockIdTickerDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DividendService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final NasdaqBalticDividendScraper nasdaqBalticDividendScraper;

    private final DividendRepository dividendRepository;

    private final Validator validator;

    private final StockRepository stockRepository;

    private final EntityManager entityManager;

    public void updateDividendInformation(int year) {
        LOG.info("updating dividend information at year {}", year);

        Map<String, Long> stockIds = stockRepository.findAllBy(StockIdTickerDto.class)
                .stream().collect(Collectors.toMap(StockIdTickerDto::getTicker, StockIdTickerDto::getId));

        List<DividendTickerExDividendDateDto> existingDividends = getExistingDividends(year);
        List<Dividend> newDividends = nasdaqBalticDividendScraper.loadYearDividends(year).stream()
                .filter(dto -> !exists(dto, existingDividends))
                .filter(dto -> stockExists(dto, stockIds))
                .map(dto -> toDividend(dto, stockIds))
                .collect(Collectors.toList());
        saveDividends(newDividends);
    }

    private boolean stockExists(DividendDto dto, Map<String, Long> stockIds) {
        return stockIds.containsKey(dto.getTicker());
    }

    private static boolean exists(DividendDto dto, List<DividendTickerExDividendDateDto> existingDividends) {
        for (DividendTickerExDividendDateDto existingDividend : existingDividends) {
            if (existingDividend.getTicker().equals(dto.getTicker()) &&
                    existingDividend.getExDividendDate().equals(dto.getExDividendDate()) &&
            existingDividend.isCapitalDecrease() == dto.isCapitalDecrease()) {
                return true;
            }
        }
        return false;
    }

    private List<DividendTickerExDividendDateDto> getExistingDividends(int year) {
        return dividendRepository.findAllByYear(year);
    }

    private Dividend toDividend(DividendDto dto, Map<String, Long> stockIds) {
        return Dividend.builder()
                .amount(dto.getAmount())
                .capitalDecrease(dto.isCapitalDecrease())
                .exDividendDate(dto.getExDividendDate())
                .stock(entityManager.getReference(Stock.class, stockIds.get(dto.getTicker())))
                .build();
    }

    private void saveDividends(List<Dividend> newDividends) {
        for (Dividend dividend : newDividends) {
            Set<ConstraintViolation<Dividend>> errors = validator.validate(dividend);
            if (CollectionUtils.isEmpty(errors)) {
                LOG.debug("adding new dividend {}", dividend);
                dividendRepository.save(dividend);
            } else {
                LOG.warn("dividend {} validation failed {}", dividend, errors);
            }
        }
    }

}
