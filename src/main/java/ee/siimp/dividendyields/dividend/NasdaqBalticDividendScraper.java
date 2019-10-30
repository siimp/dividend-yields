package ee.siimp.dividendyields.dividend;

import ee.siimp.dividendyields.common.utils.DateUtils;
import ee.siimp.dividendyields.stock.Stock;
import ee.siimp.dividendyields.stock.StockRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
class NasdaqBalticDividendScraper {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String EVENT_CAPITAL_DECREASE = "Capital decrease payment date";

    private final RestTemplate restTemplate;

    private final DividendProperties dividendProperties;

    private final StockRepository stockRepository;

    private final EntityManager entityManager;

    List<Dividend> loadYearDividends(int year) {
        List<Dividend> result = new ArrayList<>();

        try {
            Iterator<Row> rows = getXslsSheet().rowIterator();
            rows.next(); // skip header
            rows.forEachRemaining((Row row) -> {
                Optional<Dividend> dividend = getNewDividend(row);
                dividend.ifPresentOrElse(result::add, () ->  {
                    LOG.warn("skipping dividend for row {}", row);
                });
            });
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Collections.emptyList();
        }

        return result;
    }

    private Optional<Dividend> getNewDividend(Row row) {
        Stock stock = getStock(row.getCell(Header.TICKER.ordinal()).getStringCellValue());
        if (stock == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(getDividendFromRow(row, stock));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    private Dividend getDividendFromRow(Row row, Stock stock) {
        LOG.info("Parsing dividend for {}", row.getCell(Header.TICKER.ordinal()).getStringCellValue());

        Dividend dividend = new Dividend();
        dividend.setStock(stock);
        dividend.setAmount(BigDecimal.valueOf(row.getCell(Header.AMOUNT.ordinal()).getNumericCellValue()));
        dividend.setExDividendDate(DateUtils.parseEstonianDate(row.getCell(Header.DATE.ordinal()).getStringCellValue()));
        String event = row.getCell(Header.EVENT.ordinal()).getStringCellValue();
        dividend.setCapitalDecrease(EVENT_CAPITAL_DECREASE.equals(event));
        return dividend;
    }

    private Stock getStock(String ticker) {
        return stockRepository.findIdByTicker(ticker)
                .map(id -> entityManager.getReference(Stock.class, id))
                .orElse(null);
    }

    private Sheet getXslsSheet() throws IOException {
        try (InputStream inputStream = getXslsInputStream()) {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
            return xssfWorkbook.getSheetAt(0);
        }
    }

    private InputStream getXslsInputStream() {
        LOG.debug("loading remote file from {}", dividendProperties.getEndpoint());
        String response = restTemplate.getForObject(dividendProperties.getEndpoint(), String.class);
        return new ByteArrayInputStream(response.getBytes());
    }

    enum Header {
        ISSUER,
        TICKER,
        MARKET,
        DATE,
        EVENT,
        AMOUNT
    }

}