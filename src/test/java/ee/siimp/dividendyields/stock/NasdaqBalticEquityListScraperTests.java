package ee.siimp.dividendyields.stock;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class NasdaqBalticEquityListScraperTests {

    private static NasdaqBalticEquityListScraper nasdaqBalticEquityListScraper;

    @BeforeAll
    public static void setup() throws IOException {
        StockProperties stockProperties = new StockProperties();
        try (InputStream inputStream = NasdaqBalticEquityListScraperTests.class.getResourceAsStream("/shares_20191016.xlsx")) {
            stockProperties.setStaticList(new ByteArrayResource(inputStream.readAllBytes()));
            nasdaqBalticEquityListScraper =
                    new NasdaqBalticEquityListScraper(null, stockProperties);
        }
    }

    @Test
    public void shouldParseSuccessfully() {
        List<Stock> stocks = nasdaqBalticEquityListScraper.loadAllStocks();
        assertThat(stocks).isNotEmpty();
    }
}
