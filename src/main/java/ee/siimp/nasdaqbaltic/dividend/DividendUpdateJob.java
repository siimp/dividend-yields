package ee.siimp.nasdaqbaltic.dividend;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
@AllArgsConstructor
class DividendUpdateJob {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private DividendService dividendService;

    @Scheduled(cron = "0 0 6 * * SAT")
    void execute() {
        LOG.info("executing");
        dividendService.updateDividendInformation();
        LOG.info("done");
    }
}
