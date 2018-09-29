package ee.siimp.nasdaqbaltic.dividend;

import ee.siimp.nasdaqbaltic.common.service.NasdaqBalticDividendService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;

@Service
@Transactional
@AllArgsConstructor
public class DividendService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final int INFORMATION_PERIOD_IN_YEARS = 4;

    private NasdaqBalticDividendService nasdaqBalticDividendService;

    private DividendRepository dividendRepository;

    public void updateDividendInformation() {
        int toYear = LocalDate.now().getYear() + 1;
        int fromYear = toYear - INFORMATION_PERIOD_IN_YEARS;
        LOG.info("updating dividend information from year {} to year {}", fromYear, toYear);

        for (int year = fromYear; year <= toYear; year++) {
            try {
                if (isUpdateNeededForYear(year)) {
                    nasdaqBalticDividendService.loadYearDividends(year);
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                // just continue
            }
        }
    }

    private boolean isUpdateNeededForYear(int year) {
        int currentYear = LocalDate.now().getYear();
        return year >= currentYear || !dividendRepository.existsByYear(year);
    }
}
