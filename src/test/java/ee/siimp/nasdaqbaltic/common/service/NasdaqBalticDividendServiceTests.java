package ee.siimp.nasdaqbaltic.common.service;

import ee.siimp.nasdaqbaltic.dividend.DividendRepository;
import ee.siimp.nasdaqbaltic.stock.Stock;
import ee.siimp.nasdaqbaltic.stock.StockRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class NasdaqBalticDividendServiceTests {

    private static final String TEST_STOCK_TICKER = "SFG1T";

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private NasdaqBalticDividendService nasdaqBalticDividendService;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private DividendRepository dividendRepository;

    @Value("dividendsAndCapitalDecrease2018.html")
    private Resource dividendsAndCapitalDecrease2018Html;

    @Before
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
        nasdaqBalticDividendService.loadYearDividends(2018);

        assertThat(dividendRepository.count()).isEqualTo(2);
        assertThat(dividendRepository.findByStockIdAndExDividendDate(1L, LocalDate.of(2018, 01, 25)).isCapitalDecrease())
                .isEqualTo(false);
        assertThat(dividendRepository.findByStockIdAndExDividendDate(1L, LocalDate.of(2018, 07, 16)).isCapitalDecrease())
                .isEqualTo(true);
    }
}
