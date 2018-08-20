package ee.siimp.nasdaqbaltic.common;

import ee.siimp.nasdaqbaltic.stock.Stock;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class NasdaqBalticStockService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Value("${nasdaqbaltic.stock-endpoint}")
    private String nasdaqBalticStockEndpoint;

    @Value("${nasdaqbaltic.equity-list:#{null}}")
    private Resource nasdaqBalticEquityList;

    public List<Stock> loadAllStocks() throws IOException {

        List<Stock> result = new ArrayList<>();
        for (CSVRecord csvRecord : getCsvRecords()) {
            Stock stock = new Stock();
            stock.setName(csvRecord.get(NasdaqBalticCsvUtil.Header.NAME));
            stock.setIsin(csvRecord.get(NasdaqBalticCsvUtil.Header.ISIN));
            stock.setCurrency(csvRecord.get(NasdaqBalticCsvUtil.Header.CURRENCY));
            stock.setTicker(csvRecord.get(NasdaqBalticCsvUtil.Header.TICKER));
            stock.setMarketPlace(csvRecord.get(NasdaqBalticCsvUtil.Header.MARKET_PLACE));
            stock.setSegment(csvRecord.get(NasdaqBalticCsvUtil.Header.SEGMENT));
            result.add(stock);
        }

        return result;
    }

    private CSVParser getCsvRecords() throws IOException {
        if (nasdaqBalticEquityList != null) {
            LOG.debug("loading local csv file {}", nasdaqBalticEquityList.getFilename());
            return new CSVParser(
                    new BufferedReader(new InputStreamReader(nasdaqBalticEquityList.getInputStream(), StandardCharsets.UTF_16)),
                    NasdaqBalticCsvUtil.FORMAT);
        } else {
            LOG.debug("loading remote csv file from {}", nasdaqBalticStockEndpoint);

            String response = NasdaqBalticUtil.getRemoteResponse(nasdaqBalticStockEndpoint);
            return new CSVParser(new StringReader(response), NasdaqBalticCsvUtil.FORMAT);
        }

    }

}
