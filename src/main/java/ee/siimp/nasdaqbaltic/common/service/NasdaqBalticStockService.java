package ee.siimp.nasdaqbaltic.common.service;

import ee.siimp.nasdaqbaltic.common.csv.NasdaqBalticEquityListCsv;
import ee.siimp.nasdaqbaltic.stock.Stock;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
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
public class NasdaqBalticStockService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private RestTemplate restTemplate;

    @Value("${nasdaqbaltic.stock-endpoint}")
    private String nasdaqBalticStockEndpoint;

    @Value("${nasdaqbaltic.equity-list:#{null}}")
    private Resource nasdaqBalticEquityListCsvFile;

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
        stock.setName(csvRecord.get(NasdaqBalticEquityListCsv.Header.NAME));
        stock.setIsin(csvRecord.get(NasdaqBalticEquityListCsv.Header.ISIN));
        stock.setCurrency(csvRecord.get(NasdaqBalticEquityListCsv.Header.CURRENCY));
        stock.setTicker(csvRecord.get(NasdaqBalticEquityListCsv.Header.TICKER));
        stock.setMarketPlace(csvRecord.get(NasdaqBalticEquityListCsv.Header.MARKET_PLACE));
        stock.setSegment(csvRecord.get(NasdaqBalticEquityListCsv.Header.SEGMENT));
        return stock;
    }

    private CSVParser getCsvRecords() throws IOException {
        if (nasdaqBalticEquityListCsvFile != null) {
            LOG.debug("loading local csv file {}", nasdaqBalticEquityListCsvFile.getFilename());
            return new CSVParser(
                    new BufferedReader(new InputStreamReader(nasdaqBalticEquityListCsvFile.getInputStream(), StandardCharsets.UTF_16)),
                    NasdaqBalticEquityListCsv.FORMAT);
        } else {
            LOG.debug("loading remote csv file from {}", nasdaqBalticStockEndpoint);

            String response = restTemplate.getForObject(nasdaqBalticStockEndpoint, String.class);
            return new CSVParser(new StringReader(response), NasdaqBalticEquityListCsv.FORMAT);
        }

    }

}