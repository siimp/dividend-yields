package ee.siimp.dividendyields.stockinfo;

import ee.siimp.dividendyields.IntegrationTest;
import ee.siimp.dividendyields.stock.StockRepository;
import ee.siimp.dividendyields.stock.StockService;
import ee.siimp.dividendyields.stockinfo.dto.StockAndIsinDto;
import ee.siimp.dividendyields.stockinfo.dto.StockInfoDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NasdaqBalticStockInfoScraperTests extends IntegrationTest {

    private static final String TEST_STOCK_TICKER = "APG1L";

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private NasdaqBalticStockInfoScraper nasdaqBalticStockInfoScraper;

    @Autowired
    private StockRepository stockRepository;

    @Value("stockInfoApranga.html")
    private Resource stockInfoAprangaHtml;

    @Autowired
    private StockService stockService;

    @BeforeAll
    public void setupClass() {
        stockService.updateStockInformation();
    }

    @Test
    public void savesDividendSuccessfully() throws IOException {
        given(restTemplate.getForObject(any(), eq(String.class)))
                .willReturn(Files.lines(stockInfoAprangaHtml.getFile().toPath())
                        .collect(Collectors.joining()));

        Optional<Long> stockOptional = stockRepository.findIdByTicker(TEST_STOCK_TICKER);

        Optional<StockInfoDto> stockInfoOptional = nasdaqBalticStockInfoScraper.scrapeStockInfo(new StockAndIsinDto(stockOptional.get(),
                TEST_STOCK_TICKER));

        BigInteger expectedNumberOfSecuritiesValue = new BigInteger("55291960");
        assertThat(stockInfoOptional.isPresent()).isTrue();
        assertThat(stockInfoOptional.get().getNumberOfSecurities()).isEqualTo(expectedNumberOfSecuritiesValue);
    }
}
