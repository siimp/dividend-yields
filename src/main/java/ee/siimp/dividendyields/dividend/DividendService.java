package ee.siimp.dividendyields.dividend;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import ee.siimp.dividendyields.dividend.dto.DividendDto;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class DividendService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final int INFORMATION_PERIOD_IN_YEARS = 4;

    private final NasdaqBalticDividendScraper nasdaqBalticDividendScraper;

    private final DividendRepository dividendRepository;

    private final Validator validator;

    public void updateDividendInformation() {
        int toYear = LocalDate.now().getYear() + 1;
        int fromYear = toYear - INFORMATION_PERIOD_IN_YEARS;
        LOG.info("updating dividend information from year {} to year {}", fromYear, toYear);

        for (int year = fromYear; year <= toYear; year++) {
            try {
                if (isUpdateNeededForYear(year)) {
                    List<Dividend> existingDividends = getExistingDividends(year);
                    List<Dividend> newDividends = nasdaqBalticDividendScraper.loadYearDividends(year).stream()
                            .filter(dto -> !exists(dto, existingDividends))
                            .map(this::toDividend)
                            .collect(Collectors.toList());
                    saveDividends(newDividends);
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                // just continue
            }
        }
    }

    private static boolean exists(DividendDto dto, List<Dividend> existingDividends) {
        // TODO
        return false;
    }

    private List<Dividend> getExistingDividends(int year) {
        // TODO
        return Collections.emptyList();
    }

    private Dividend toDividend(DividendDto dto) {
        Dividend dividend = new Dividend();
        // TODO
        return dividend;
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

    private boolean isUpdateNeededForYear(int year) {
        int currentYear = LocalDate.now().getYear();
        return year >= currentYear || !dividendRepository.existsByYear(year);
    }
}
