package ee.siimp.dividendyields.dividend;

import ee.siimp.dividendyields.common.utils.DateUtils;
import ee.siimp.dividendyields.dividend.dto.DividendDto;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
class NasdaqBalticDividendScraper {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String EVENT_CAPITAL_DECREASE_EX_DATE = "Capital decrease ex-date";

    private static final String EVENT_DIVIDEND_EX_DATE = "Dividend ex-date";

    private final RestTemplate restTemplate;

    private final DividendProperties dividendProperties;

    List<DividendDto> loadYearDividends(int year) {
        LOG.info("loading dividends by year {}", year);
        List<DividendDto> result = new ArrayList<>();

        try {
            Iterator<Row> rows = getXslsSheet(year).rowIterator();
            rows.next(); // skip header
            rows.forEachRemaining((Row row) -> {
                Optional<DividendDto> dividend = getNewDividend(row);
                if (dividend.isPresent()) {
                    DividendDto dto = dividend.get();
                    LOG.debug("found dividend with amount {} for {}", dto.getAmount(), dto.getTicker());
                    result.add(dto);
                }
            });
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        LOG.info("finished loading dividends");
        return result;
    }

    private Optional<DividendDto> getNewDividend(Row row) {
        try {
            return Optional.ofNullable(getDividendFromRow(row));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    private DividendDto getDividendFromRow(Row row) {
        if (row == null || row.getCell(Header.EVENT.ordinal()) == null) {
            return null;
        }


        String event = row.getCell(Header.EVENT.ordinal()).getStringCellValue();
        if (!(EVENT_DIVIDEND_EX_DATE.equals(event) || EVENT_CAPITAL_DECREASE_EX_DATE.equals(event))) {
            LOG.debug("skipping event {}", event);
            return null;
        }

        String ticker = row.getCell(Header.TICKER.ordinal()).getStringCellValue();
        LOG.debug("Parsing dividend event \"{}\" for {}", event, ticker);

        DividendDto result = DividendDto.builder()
                .ticker(ticker)
                .issuer(row.getCell(Header.ISSUER.ordinal()).getStringCellValue())
                .market(row.getCell(Header.MARKET.ordinal()).getStringCellValue())
                .amount(BigDecimal.valueOf(row.getCell(Header.AMOUNT.ordinal()).getNumericCellValue()))
                .exDividendDate(DateUtils.convertToLocalDate(row.getCell(Header.DATE.ordinal()).getDateCellValue()))
                .capitalDecrease(EVENT_CAPITAL_DECREASE_EX_DATE.equals(event))
                .build();
        LOG.debug("Parsed successfully dividend event \"{}\" for {}", event, ticker);
        return result;
    }

    private Sheet getXslsSheet(int year) throws IOException {
        try (InputStream inputStream = getXslsInputStream(year)) {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
            return xssfWorkbook.getSheetAt(0);
        }
    }

    private InputStream getXslsInputStream(int year) throws IOException {
        if (dividendProperties.getStaticList() != null) {
            LOG.debug("loading local static file {}", dividendProperties.getStaticList().getFilename());
            return dividendProperties.getStaticList().getInputStream();
        } else {
            String endpointUri = UriComponentsBuilder.fromHttpUrl(dividendProperties.getEndpoint())
                    .queryParam("filter", "1")
                    .queryParam("from", year + "-01-01")
                    .queryParam("to", year + "12-31")
                    .toUriString();
            LOG.debug("loading remote file from {}", endpointUri);
            String response = restTemplate.getForObject(endpointUri, String.class);
            return new ByteArrayInputStream(response.getBytes());
        }
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