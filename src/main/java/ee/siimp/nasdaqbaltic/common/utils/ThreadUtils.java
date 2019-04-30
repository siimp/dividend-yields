package ee.siimp.nasdaqbaltic.common.utils;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Random;

@UtilityClass
public class ThreadUtils {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final Random random = new Random();

    private static final int DEFAULT_SLEEP_IN_MILLISECONDS = 5000;

    public static void randomSleep() {
        try {
            Thread.sleep(random.nextInt(DEFAULT_SLEEP_IN_MILLISECONDS));
        } catch (InterruptedException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
