package ee.siimp.dividendyields.stock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ee.siimp.dividendyields.stock.dto.StockDto;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
class NasdaqBalticStockListScraper {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final RestTemplate restTemplate;

    private final StockProperties stockProperties;

    List<StockDto> loadAllStocks() {
        List<StockDto> result = new ArrayList<>();
        try {
            Iterator<Row> rows = getXslsSheet().rowIterator();
            rows.next(); // skip header
            rows.forEachRemaining((Row row) -> {
                StockDto stockDto = getNewStock(row);
                result.add(stockDto);
            });
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return Collections.emptyList();
        }

        return result;
    }

    private StockDto getNewStock(Row row) {
        String ticker = row.getCell(Header.TICKER.ordinal()).getStringCellValue();
        LOG.debug("parsing stock {}", ticker);
        StockDto result = StockDto.builder()
                .name(row.getCell(Header.NAME.ordinal()).getStringCellValue())
                .isin(row.getCell(Header.ISIN.ordinal()).getStringCellValue())
                .currency(row.getCell(Header.CURRENCY.ordinal()).getStringCellValue())
                .ticker(ticker)
                .marketPlace(row.getCell(Header.MARKET_PLACE.ordinal()).getStringCellValue())
                .segment(row.getCell(Header.SEGMENT.ordinal()).getStringCellValue())
                .build();
        LOG.debug("parsed stock {} successfully", ticker);
        return result;
    }

    private Sheet getXslsSheet() throws IOException {
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
            LOG.debug("loading remote file from {}", stockProperties.getEndpoint());
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