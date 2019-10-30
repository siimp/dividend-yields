package ee.siimp.dividendyields.dividend;

import ee.siimp.dividendyields.stock.NasdaqBalticEquityListScraperTests;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;


public class NasdaqBalticDividendScraperTests {

    private static final String TEST_STOCK_TICKER = "SFG1T";

    private static final String DIVIDENDS_XLSX = "/dividends_20191016.xlsx";

    private static NasdaqBalticDividendScraper nasdaqBalticDividendScraper;

    @BeforeAll
    public static void setUp() throws IOException {
        DividendProperties dividendProperties = new DividendProperties();
        try (InputStream inputStream = NasdaqBalticEquityListScraperTests.class.getResourceAsStream(DIVIDENDS_XLSX)) {
            // stockProperties.setStaticList(new ByteArrayResource(inputStream.readAllBytes()));
            nasdaqBalticDividendScraper = new NasdaqBalticDividendScraper(null, dividendProperties);
        }
    }

    @Test
    public void savesDividendSuccessfully() {
        nasdaqBalticDividendScraper.loadYearDividends(2019);
    }
}
