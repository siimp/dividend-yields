package ee.siimp.dividendyields.dividend;

import ee.siimp.dividendyields.common.XlsxScraper;
import ee.siimp.dividendyields.common.utils.DateUtils;
import ee.siimp.dividendyields.dividend.dto.DividendDto;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class NasdaqBalticDividendScraper extends XlsxScraper<DividendDto> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String EVENT_CAPITAL_DECREASE_EX_DATE = "Capital decrease ex-date";

    private static final String EVENT_DIVIDEND_EX_DATE = "Dividend ex-date";

    private final DividendProperties dividendProperties;

    private static final String YEAR_PARAMETER = "year";

    List<DividendDto> scrapeYearDividends(int year) {
        setParameter(YEAR_PARAMETER, year);
        LOG.info("loading dividends by year {}", year);
        List<DividendDto> result = processAllRows();
        LOG.info("finished loading dividends, result size is {}", result.size());
        return result;
    }

    @Override
    protected String getEndpoint() {
        return UriComponentsBuilder.fromHttpUrl(dividendProperties.getEndpoint())
                .queryParam("filter", "1")
                .queryParam("from", getParameter(YEAR_PARAMETER) + "-01-01")
                .queryParam("to", getParameter(YEAR_PARAMETER) + "12-31")
                .toUriString();
    }

    @Override
    protected Resource getStaticResource() {
        return dividendProperties.getStaticList();
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    protected Optional<DividendDto> processRow(Row row) {
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
            LOG.trace("skipping event {}", event);
            return null;
        }

        if (row.getCell(Header.TICKER.ordinal()) == null || row.getCell(Header.AMOUNT.ordinal()) == null) {
            LOG.debug("skipping event {}, because missing ticker or amount value", event);
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

    enum Header {
        ISSUER,
        TICKER,
        MARKET,
        DATE,
        EVENT,
        AMOUNT
    }

}