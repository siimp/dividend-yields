package ee.siimp.nasdaqbaltic.common;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

@UtilityClass
public class NasdaqBalticUtil {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static String getRemoteResponse(String endpoint) throws MalformedURLException {
        URL url = new URL(endpoint);
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(url.openStream()));) {
            return in.lines().collect(Collectors.joining());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

}
