package ee.siimp.nasdaqbaltic.dividend;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
@RequiredArgsConstructor
class DividendUpdateJob {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final DividendService dividendService;

    @Scheduled(cron = "#{dividendProperties.getUpdateJobCron()}")
    void execute() {
        LOG.info("executing");
        dividendService.updateDividendInformation();
        LOG.info("done");
    }
}
