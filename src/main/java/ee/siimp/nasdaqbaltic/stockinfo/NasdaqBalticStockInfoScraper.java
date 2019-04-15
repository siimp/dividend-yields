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
class NasdaqBalticStockInfoScraper {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String URI_QUERY_PARAM_INSTRUMENT = "instrument";

    private final StockInfoRepository stockInfoRepository;

    private final StockInfoProperties stockInfoProperties;

    private final RestTemplate restTemplate;

    private final EntityManager em;

    void loadStockInfo(Long stockId, String stockIsin) {
        URI endpoint = getEndpoint(stockIsin);
        LOG.info("loading price for {} from {}", stockIsin, endpoint);
        String response = restTemplate.getForObject(endpoint, String.class);


        try {
            saveStockInfo(stockId, getNumberOfSecurities(response));
        } catch (Exception e) {
            LOG.info("could not get stock info for {} with stock id = {}",
                    stockIsin, stockId);
            LOG.error(e.getMessage());
        }
    }

    private void saveStockInfo(Long stockId, BigInteger numberOfSecurities) {
        LOG.info("saving stock info with number of securities {} for stock id = {}", numberOfSecurities, stockId);
        boolean stockInfoExists =
                stockInfoRepository.existsByStockIdAndNumberOfSecurities(stockId, numberOfSecurities);

        if (stockInfoExists) {
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

        return new BigInteger(value);
    }
}
