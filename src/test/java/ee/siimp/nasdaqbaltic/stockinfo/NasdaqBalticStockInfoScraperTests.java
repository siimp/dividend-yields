package ee.siimp.nasdaqbaltic.stockinfo;

import ee.siimp.nasdaqbaltic.IntegrationTest;
import ee.siimp.nasdaqbaltic.stock.Stock;
import ee.siimp.nasdaqbaltic.stock.StockRepository;
import ee.siimp.nasdaqbaltic.stockinfo.dto.StockAndIsinDto;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

public class NasdaqBalticStockInfoScraperTests extends IntegrationTest {

    private static final String TEST_STOCK_TICKER = "APG1L";

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private NasdaqBalticStockInfoScraper nasdaqBalticStockInfoScraper;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockInfoRepository stockInfoRepository;

    @Value("stockInfoApranga.html")
    private Resource stockInfoAprangaHtml;

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
    public void savesDividendSuccessfully() throws IOException {
        given(restTemplate.getForObject(any(), eq(String.class)))
                .willReturn(Files.lines(stockInfoAprangaHtml.getFile().toPath())
                        .collect(Collectors.joining()));

        Optional<Long> stockOptional = stockRepository.findIdByTicker(TEST_STOCK_TICKER);

        Optional<StockInfo> stockInfoOptional = stockInfoRepository.
                findByStockId(stockOptional.get());
        assertThat(stockInfoOptional.isPresent()).isFalse();

        nasdaqBalticStockInfoScraper.loadStockInfo(new StockAndIsinDto(stockOptional.get(),
                TEST_STOCK_TICKER, TEST_STOCK_TICKER));

        BigInteger expectedNumberOfSecuritiesValue = new BigInteger("55291960");
        stockInfoOptional = stockInfoRepository.
                findByStockId(stockOptional.get());
        assertThat(stockInfoOptional.isPresent()).isTrue();
        assertThat(stockInfoOptional.get().getNumberOfSecurities()).isEqualTo(expectedNumberOfSecuritiesValue);
    }
}
