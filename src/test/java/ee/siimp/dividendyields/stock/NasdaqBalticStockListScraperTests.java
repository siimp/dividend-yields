package ee.siimp.dividendyields.stock;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class NasdaqBalticStockListScraperTests {

    private static final String SHARES_XLSX = "/shares_20191016.xlsx";

    private static NasdaqBalticStockListScraper nasdaqBalticStockListScraper;

    @BeforeAll
    public static void setup() throws IOException {
        StockProperties stockProperties = new StockProperties();
        try (InputStream inputStream = NasdaqBalticStockListScraperTests.class.getResourceAsStream(SHARES_XLSX)) {
            stockProperties.setStaticList(new ByteArrayResource(inputStream.readAllBytes()));
            nasdaqBalticStockListScraper =
                    new NasdaqBalticStockListScraper(null, stockProperties);
        }
    }

    @Test
    public void shouldParseSuccessfully() {
        List<Stock> stocks = nasdaqBalticStockListScraper.loadAllStocks();
        assertThat(stocks).isNotEmpty();
    }
}
