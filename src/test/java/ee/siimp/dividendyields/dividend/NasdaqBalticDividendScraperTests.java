package ee.siimp.dividendyields.dividend;

import ee.siimp.dividendyields.IntegrationTest;
import ee.siimp.dividendyields.stock.Stock;
import ee.siimp.dividendyields.stock.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestTemplate;

import javax.script.ScriptException;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;


public class NasdaqBalticDividendScraperTests extends IntegrationTest {

    private static final String TEST_STOCK_TICKER = "SFG1T";

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private NasdaqBalticDividendScraper nasdaqBalticDividendScraper;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private DividendRepository dividendRepository;

    @Value("dividendsAndCapitalDecrease2018.html")
    private Resource dividendsAndCapitalDecrease2018Html;

    @BeforeEach
    public void setUp() {
        Stock stock = new Stock();
        stock.setName(TEST_STOCK_TICKER);
        stock.setIsin(TEST_STOCK_TICKER);
        stock.setCurrency(TEST_STOCK_TICKER);
        stock.setTicker(TEST_STOCK_TICKER);
        stock.setMarketPlace(TEST_STOCK_TICKER);
        stock.setSegment(TEST_STOCK_TICKER);
        stockRepository.save(stock);
    }

    @Test
    public void savesDividendSuccessfully() throws ScriptException, IOException {
        given(restTemplate.getForObject(any(), eq(String.class)))
                .willReturn(Files.lines(dividendsAndCapitalDecrease2018Html.getFile().toPath())
                        .collect(Collectors.joining()));
        nasdaqBalticDividendScraper.loadYearDividends(2018);

        assertThat(dividendRepository.count()).isEqualTo(2);
        assertThat(dividendRepository.findByStockIdAndExDividendDate(1L, LocalDate.of(2018, 1, 25)).isCapitalDecrease())
                .isEqualTo(false);
        assertThat(dividendRepository.findByStockIdAndExDividendDate(1L, LocalDate.of(2018, 7, 16)).isCapitalDecrease())
                .isEqualTo(true);
    }
}
