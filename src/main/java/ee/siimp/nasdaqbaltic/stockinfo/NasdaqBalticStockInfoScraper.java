package ee.siimp.nasdaqbaltic.stockinfo;

import ee.siimp.nasdaqbaltic.stock.Stock;
import ee.siimp.nasdaqbaltic.stockinfo.dto.StockAndIsinDto;
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

@Service
@RequiredArgsConstructor
class NasdaqBalticStockInfoScraper {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String URI_QUERY_PARAM_INSTRUMENT = "instrument";

    private static final String URI_QUERY_PARAM_LIST = "list";

    private final StockInfoRepository stockInfoRepository;

    private final StockInfoProperties stockInfoProperties;

    private final RestTemplate restTemplate;

    private final EntityManager entityManager;

    void loadStockInfo(StockAndIsinDto stockAndIsinDto) {
        boolean stockInfoExists = stockInfoRepository.existsByStockId(stockAndIsinDto.getId());

        if (stockInfoExists) {
            LOG.info("this stock info is already saved");
            return;
        }


        URI endpoint = getEndpoint(stockAndIsinDto.getIsin(), stockAndIsinDto.getSegment());
        LOG.info("loading price for {} from {}", stockAndIsinDto.getIsin(), endpoint);
        String response = restTemplate.getForObject(endpoint, String.class);


        try {
            saveStockInfo(stockAndIsinDto.getId(), getNumberOfSecurities(response));
        } catch (Exception e) {
            LOG.info("could not get stock info for {}", stockAndIsinDto.getIsin());
            LOG.error(e.getMessage());
        }
    }

    private void saveStockInfo(Long stockId, BigInteger numberOfSecurities) {
        LOG.info("saving stock info with number of securities {} for stock = {}", numberOfSecurities, stockId);

        StockInfo stockInfo = new StockInfo();
        stockInfo.setStock(entityManager.getReference(Stock.class, stockId));
        stockInfo.setNumberOfSecurities(numberOfSecurities);
        stockInfoRepository.save(stockInfo);
    }

    private URI getEndpoint(String stockIsin, String segment) {
        return UriComponentsBuilder.fromHttpUrl(stockInfoProperties.getEndpoint())
                .queryParam(URI_QUERY_PARAM_INSTRUMENT, stockIsin)
                .queryParam(URI_QUERY_PARAM_LIST, getSegmentNumberFromName(segment))
                .build().toUri();
    }

    private int getSegmentNumberFromName(String segment) {
        if (Stock.SEGMENT_MAIN_LIST.equals(segment)) {
            return 2;
        } else if (Stock.SEGMENT_SECONDARY_LIST.equals(segment)) {
            return 3;
        } else if (Stock.SEGMENT_FIRST_NORTH_LIST.equals(segment)) {
            return 6;
        } else {
            return 1;
        }
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
