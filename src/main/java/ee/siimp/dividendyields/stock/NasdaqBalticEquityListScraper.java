package ee.siimp.dividendyields.stock;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
class NasdaqBalticEquityListScraper {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final RestTemplate restTemplate;

    private final StockProperties stockProperties;


    List<Stock> loadAllStocks()  {

        List<Stock> result = new ArrayList<>();
        try {
            Iterator<Row> rows = getXslsSheet().rowIterator();
            rows.next(); // skip header
            rows.forEachRemaining((Row row) -> {
                Stock stock = getNewStock(row);
                result.add(stock);
            });
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return Collections.emptyList();
        }

        return result;
    }

    private Stock getNewStock(Row row) {
        Stock stock = new Stock();

        stock.setName(row.getCell(Header.NAME.ordinal()).getStringCellValue());
        stock.setIsin(row.getCell(Header.ISIN.ordinal()).getStringCellValue());
        stock.setCurrency(row.getCell(Header.CURRENCY.ordinal()).getStringCellValue());
        stock.setTicker(row.getCell(Header.TICKER.ordinal()).getStringCellValue());
        stock.setMarketPlace(row.getCell(Header.MARKET_PLACE.ordinal()).getStringCellValue());
        stock.setSegment(row.getCell(Header.SEGMENT.ordinal()).getStringCellValue());

        return stock;
    }

    private XSSFSheet getXslsSheet() throws IOException {
        try (InputStream inputStream = getXslsInputStream()) {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
            return xssfWorkbook.getSheetAt(0);
        }
    }

    private InputStream getXslsInputStream() throws IOException {
        if (stockProperties.getStaticList() != null) {
            LOG.debug("loading local static file {}", stockProperties.getStaticList().getFilename());
            return stockProperties.getStaticList().getInputStream();
        } else {
            LOG.debug("loading remote  file from {}", stockProperties.getEndpoint());
            String response = restTemplate.getForObject(stockProperties.getEndpoint(), String.class);
            return new ByteArrayInputStream(response.getBytes());
        }
    }

    enum Header {
        TICKER,
        NAME,
        ISIN,
        CURRENCY,
        MARKET_PLACE,
        SEGMENT
    }
}