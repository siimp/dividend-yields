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

    private static final int DIVIDEND_STARTING_YEAR = 2015;

    private NasdaqBalticDividendService nasdaqBalticDividendService;

    private DividendRepository dividendRepository;

    public void updateDividendInformation() {
        LOG.info("updating dividend information");
        int currentYear = LocalDate.now().getYear();
        for (int year = DIVIDEND_STARTING_YEAR; year <= currentYear; year++) {
            try {
                if (year == currentYear || !dividendRepository.existsByYear(year)) {
                    nasdaqBalticDividendService.loadYearDividends(year);
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                // just continue
            }
        }
    }
}
