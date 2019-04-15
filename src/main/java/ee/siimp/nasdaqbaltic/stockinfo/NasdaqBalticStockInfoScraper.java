package ee.siimp.nasdaqbaltic.stockinfo;

import ee.siimp.nasdaqbaltic.stock.Stock;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import java.lang.invoke.MethodHandles;
import java.math.BigInteger;
import java.net.URI;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NasdaqBalticStockInfoScraper {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String URI_QUERY_PARAM_INSTRUMENT = "instrument";

    private final StockInfoRepository stockInfoRepository;

    private final StockInfoProperties stockInfoProperties;

    private final RestTemplate restTemplate;

    private final EntityManager em;

    public void loadStockInfo(Long stockId, String stockIsin) {
        URI endpoint = getEndpoint(stockIsin);
        LOG.info("loading price for {} from {}", stockIsin, endpoint);
        String response = restTemplate.getForObject(endpoint, String.class);
        BigInteger numberOfSecurities = getNumberOfSecurities(response);

        if (numberOfSecurities != null) {
            saveStockInfo(stockId, numberOfSecurities);
        }
    }

    private void saveStockInfo(Long stockId, BigInteger numberOfSecurities) {
        LOG.info("saving stock info with number of securities {} for stock id = {}", numberOfSecurities, stockId);
        Optional<StockInfo> stockInfoOptional =
                stockInfoRepository.findByStockIdAndNumberOfSecurities(stockId, numberOfSecurities);

        if (stockInfoOptional.isPresent()) {
            LOG.info("this stock info is already saved");
        } else {
            StockInfo stockInfo = new StockInfo();
            stockInfo.setStock(em.getReference(Stock.class, stockId));
            stockInfo.setNumberOfSecurities(numberOfSecurities);
            stockInfoRepository.save(stockInfo);
        }
    }

    private URI getEndpoint(String stockIsin) {
        return UriComponentsBuilder.fromHttpUrl(stockInfoProperties.getEndpoint())
                .queryParam(URI_QUERY_PARAM_INSTRUMENT, stockIsin)
                .build().toUri();
    }

    private BigInteger getNumberOfSecurities(String response) {
        int beginIndex = response.indexOf("Noteeritud väärtpabereid");
        int endIndex = response.indexOf("</tr>", beginIndex);
        String labelAndValue = response.substring(beginIndex, endIndex);

        String valueBeginToken = "colTypeText\">";
        int valueBeginIndex = labelAndValue.indexOf(valueBeginToken);
        int valueEndIndex = labelAndValue.indexOf("</td>", valueBeginIndex);
        String value = labelAndValue.substring(valueBeginIndex + valueBeginToken.length(),
                valueEndIndex).replaceAll(" ", "");

        LOG.info("read number of securities value {}", value);
        try {
            return new BigInteger(value);
        } catch (NumberFormatException e) {
            LOG.error(e.getMessage(), e);
        }

        return null;
    }
}
