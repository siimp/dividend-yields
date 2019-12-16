package ee.siimp.dividendyields.dividend;

import ee.siimp.dividendyields.dividend.dto.DividendDto;
import ee.siimp.dividendyields.stock.NasdaqBalticStockListScraperTests;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class NasdaqBalticDividendScraperTests {

    private static final String DIVIDENDS_XLSX = "/dividends_20191030.xlsx";

    private static NasdaqBalticDividendScraper nasdaqBalticDividendScraper;

    @BeforeAll
    public static void setUp() throws IOException {
        DividendProperties dividendProperties = new DividendProperties();
        try (InputStream inputStream = NasdaqBalticStockListScraperTests.class.getResourceAsStream(DIVIDENDS_XLSX)) {
            dividendProperties.setStaticList(new ByteArrayResource(inputStream.readAllBytes()));
            nasdaqBalticDividendScraper = new NasdaqBalticDividendScraper(dividendProperties);
        }
    }

    @Test
    public void savesDividendSuccessfully() {
        List<DividendDto> dividends = nasdaqBalticDividendScraper.loadYearDividends(2019);
        assertThat(dividends.size()).isEqualTo(43);
    }
}
