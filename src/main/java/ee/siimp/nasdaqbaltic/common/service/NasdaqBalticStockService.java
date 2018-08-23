package ee.siimp.nasdaqbaltic.common.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ee.siimp.nasdaqbaltic.stock.Stock;

import lombok.experimental.UtilityClass;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NasdaqBalticStockService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private RestTemplate restTemplate;

    @Value("${nasdaqbaltic.stock-endpoint}")
    private String nasdaqBalticStockEndpoint;

    @Value("${nasdaqbaltic.equity-list:#{null}}")
    private Resource nasdaqBalticEquityList;

    public List<Stock> loadAllStocks() {

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
        stock.setName(csvRecord.get(NasdaqBalticCsv.Header.NAME));
        stock.setIsin(csvRecord.get(NasdaqBalticCsv.Header.ISIN));
        stock.setCurrency(csvRecord.get(NasdaqBalticCsv.Header.CURRENCY));
        stock.setTicker(csvRecord.get(NasdaqBalticCsv.Header.TICKER));
        stock.setMarketPlace(csvRecord.get(NasdaqBalticCsv.Header.MARKET_PLACE));
        stock.setSegment(csvRecord.get(NasdaqBalticCsv.Header.SEGMENT));
        return stock;
    }

    private CSVParser getCsvRecords() throws IOException {
        if (nasdaqBalticEquityList != null) {
            LOG.debug("loading local csv file {}", nasdaqBalticEquityList.getFilename());
            return new CSVParser(
                    new BufferedReader(new InputStreamReader(nasdaqBalticEquityList.getInputStream(), StandardCharsets.UTF_16)),
                    NasdaqBalticCsv.FORMAT);
        } else {
            LOG.debug("loading remote csv file from {}", nasdaqBalticStockEndpoint);

            String response = restTemplate.getForObject(nasdaqBalticStockEndpoint, String.class);
            return new CSVParser(new StringReader(response), NasdaqBalticCsv.FORMAT);
        }

    }

}

@UtilityClass
class NasdaqBalticCsv {

    private static final String[] CSV_HEADERS = {NasdaqBalticCsv.Header.TICKER, NasdaqBalticCsv.Header.NAME, NasdaqBalticCsv.Header.ISIN, NasdaqBalticCsv.Header.CURRENCY,
            NasdaqBalticCsv.Header.MARKET_PLACE, NasdaqBalticCsv.Header.SEGMENT, "Average Price", "Open Price", "High Price", "Low Price",
            "Last close Price", "Last Price", "Price Change(%)", "Best bid", "Best ask", "Trades", "Volume", "Turnover"};

    static final CSVFormat FORMAT = CSVFormat.TDF.withHeader(NasdaqBalticCsv.CSV_HEADERS).withFirstRecordAsHeader();

    @UtilityClass
    static class Header {

        static final String TICKER = "Ticker";

        static final String NAME = "Name";

        static final String ISIN = "ISIN";

        static final String CURRENCY = "Currency";

        static final String MARKET_PLACE = "MarketPlace";

        static final String SEGMENT = "List/segment";

    }
}
