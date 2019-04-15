package ee.siimp.nasdaqbaltic.stock;

import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
class NasdaqBalticEquityListScraper {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final RestTemplate restTemplate;

    private final StockProperties stockProperties;


    List<Stock> loadAllStocks() {

        List<Stock> result = new ArrayList<>();
        try {
            for (CSVRecord csvRecord : getCsvRecords()) {
                Stock stock = getNewStock(csvRecord);
                result.add(stock);
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return Collections.emptyList();
        }

        return result;
    }

    private Stock getNewStock(CSVRecord csvRecord) {
        Stock stock = new Stock();
        stock.setName(csvRecord.get(NasdaqBalticEquityListCsv.Header.NAME));
        stock.setIsin(csvRecord.get(NasdaqBalticEquityListCsv.Header.ISIN));
        stock.setCurrency(csvRecord.get(NasdaqBalticEquityListCsv.Header.CURRENCY));
        stock.setTicker(csvRecord.get(NasdaqBalticEquityListCsv.Header.TICKER));
        stock.setMarketPlace(csvRecord.get(NasdaqBalticEquityListCsv.Header.MARKET_PLACE));
        stock.setSegment(csvRecord.get(NasdaqBalticEquityListCsv.Header.SEGMENT));
        return stock;
    }

    private CSVParser getCsvRecords() throws IOException {
        if (stockProperties.getStaticList() != null) {
            LOG.debug("loading local csv file {}", stockProperties.getStaticList().getFilename());
            return new CSVParser(
                    new BufferedReader(new InputStreamReader(stockProperties.getStaticList().getInputStream(), StandardCharsets.UTF_16)),
                    NasdaqBalticEquityListCsv.FORMAT);
        } else {
            LOG.debug("loading remote csv file from {}", stockProperties.getEndpoint());

            String response = restTemplate.getForObject(stockProperties.getEndpoint(), String.class);
            return new CSVParser(new StringReader(response), NasdaqBalticEquityListCsv.FORMAT);
        }

    }

}