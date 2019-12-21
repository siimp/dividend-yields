package ee.siimp.dividendyields.stockinfo;

import ee.siimp.dividendyields.stockinfo.dto.StockAndIsinDto;
import ee.siimp.dividendyields.stockinfo.dto.StockInfoDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.invoke.MethodHandles;
import java.math.BigInteger;
import java.net.URI;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class NasdaqBalticStockInfoScraper {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final StockInfoProperties stockInfoProperties;

    private final RestTemplate restTemplate;

    Optional<StockInfoDto> scrapeStockInfo(StockAndIsinDto stockAndIsinDto) {
        URI endpoint = getEndpoint(stockAndIsinDto.getIsin());
        LOG.info("loading stock info from {}", endpoint);

        StockInfoDto dto = null;
        try {
            String response = restTemplate.getForObject(endpoint, String.class);
            BigInteger numberOfSecurities = getNumberOfSecurities(response);
            dto = StockInfoDto.builder()
                    .numberOfSecurities(numberOfSecurities)
                    .build();
        } catch (Exception e) {
            LOG.info("could not get stock info for {}", stockAndIsinDto.getIsin());
            LOG.error(e.getMessage());
        }

        return Optional.ofNullable(dto);

    }

    private URI getEndpoint(String stockIsin) {
        String endpoint = stockInfoProperties.getEndpoint().replace("{ISIN}", stockIsin);
        return UriComponentsBuilder.fromHttpUrl(endpoint)
                .build().toUri();
    }

    private BigInteger getNumberOfSecurities(String response) {
        int beginIndex = response.indexOf("Noteeritud väärtpaberite arv </td>");
        int endIndex = response.indexOf("</tr>", beginIndex);
        String labelAndValue = response.substring(beginIndex, endIndex);

        String valueBeginToken = "<td>";
        int valueBeginIndex = labelAndValue.indexOf(valueBeginToken);
        int valueEndIndex = labelAndValue.indexOf("</td>", valueBeginIndex);
        String value = labelAndValue.substring(valueBeginIndex + valueBeginToken.length(),
                valueEndIndex).replaceAll("\\p{Space}", "");

        LOG.info("read number of securities value {}", value);

        return new BigInteger(value);
    }
}
