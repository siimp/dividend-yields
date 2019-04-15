package ee.siimp.nasdaqbaltic.stockprice;

import ee.siimp.nasdaqbaltic.common.utils.DateUtils;
import ee.siimp.nasdaqbaltic.stock.Stock;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.net.URI;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class NasdaqBalticStockPriceScraper {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String URI_QUERY_PARAM_INSTRUMENT = "instrument";
    private static final String URI_QUERY_PARAM_DATE_START = "start";
    private static final String URI_QUERY_PARAM_DATE_END = "end";
    private static final int MAX_DAYS_TO_TRY_ON_VALUE_ABSENCE = 2;

    private final RestTemplate restTemplate;

    private final EntityManager em;

    private final StockPriceRepository stockPriceRepository;

    private final StockPriceProperties stockPriceProperties;

    public void loadStockPrice(Long stockId, String stockIsin, LocalDate date) {

        if (LocalDate.now().isBefore(date)) {
            LOG.warn("skipping future date {} for stock id = {}", date, stockId);
            return;
        }

        List<CSVRecord> csvRecords = getCsvRecords(stockIsin, date);

        if (CollectionUtils.isEmpty(csvRecords)) {
            int daysToSubtract = DayOfWeek.MONDAY.equals(date.getDayOfWeek()) ? 3 : 1;
            for (; daysToSubtract < daysToSubtract + MAX_DAYS_TO_TRY_ON_VALUE_ABSENCE
                    && CollectionUtils.isEmpty(csvRecords); daysToSubtract++) {
                LocalDate newDate = date.minusDays(daysToSubtract);
                LOG.info("using stock price from {} instead of {} for stock id = {}", newDate, date, stockId);
                csvRecords = getCsvRecords(stockIsin, newDate);
            }
        }

        if (!CollectionUtils.isEmpty(csvRecords)) {
            saveStockPrice(stockId, date, csvRecords);
        }
    }

    private List<CSVRecord> getCsvRecords(String stockIsin, LocalDate date) {
        URI endpoint = getEndpoint(stockIsin, date);
        LOG.info("loading price for {} from {}", stockIsin, endpoint);
        String response = restTemplate.getForObject(endpoint, String.class);

        return getCsvRecordsFromResponse(response);
    }

    private List<CSVRecord> getCsvRecordsFromResponse(String response) {
        try {
            List<CSVRecord> csvRecords = new CSVParser(new StringReader(response),
                    NasdaqBalticStockPriceCsv.FORMAT).getRecords();
            if (CollectionUtils.isEmpty(csvRecords)) {
                LOG.error("CSV is empty");
            } else if (csvRecords.size() != 1) {
                LOG.error("CSV does not contain single record (size = {})", csvRecords.size());
            } else {
                return csvRecords;
            }
        } catch (IOException | RuntimeException e) {
            LOG.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    private URI getEndpoint(String stockIsin, LocalDate date) {
        return UriComponentsBuilder.fromHttpUrl(stockPriceProperties.getEndpoint())
                .queryParam(URI_QUERY_PARAM_INSTRUMENT, stockIsin)
                .queryParam(URI_QUERY_PARAM_DATE_START, DateUtils.formatEstonianDate(date))
                .queryParam(URI_QUERY_PARAM_DATE_END, DateUtils.formatEstonianDate(date))
                .build().toUri();
    }

    private void saveStockPrice(Long stockId, LocalDate date, List<CSVRecord> csvRecords) {
        for (CSVRecord csvRecord : csvRecords) {
            saveNewStockPrice(stockId, date, new BigDecimal(csvRecord.get(NasdaqBalticStockPriceCsv.Header.LAST)));
        }
    }

    private void saveNewStockPrice(Long stockId, LocalDate date, BigDecimal price) {
        LOG.info("saving stock price {} {} for stock id = {}", price, date, stockId);
        Optional<StockPrice> stockPriceOptional = stockPriceRepository.findByStockIdAndDate(stockId, date);

        if (stockPriceOptional.isPresent()) {
            stockPriceOptional.get().setPrice(price);
            stockPriceRepository.save(stockPriceOptional.get());
        } else {
            StockPrice stockPrice = new StockPrice();
            stockPrice.setDate(date);
            stockPrice.setPrice(price);
            stockPrice.setStock(em.getReference(Stock.class, stockId));
            stockPriceRepository.save(stockPrice);
        }


    }

}
