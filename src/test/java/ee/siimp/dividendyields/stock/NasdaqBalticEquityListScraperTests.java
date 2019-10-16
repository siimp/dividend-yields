package ee.siimp.dividendyields.stock;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class NasdaqBalticEquityListScraperTests {

    private NasdaqBalticEquityListScraper nasdaqBalticEquityListScraper;

    @Before
    public void setup() throws IOException {
        StockProperties stockProperties = new StockProperties();
        try (InputStream inputStream = this.getClass().getResourceAsStream("/shares_20191016.xlsx")) {
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
