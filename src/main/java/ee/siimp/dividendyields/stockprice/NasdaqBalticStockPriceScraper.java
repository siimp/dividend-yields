package ee.siimp.dividendyields.stockprice;

import ee.siimp.dividendyields.common.XlsxScraper;
import ee.siimp.dividendyields.stockprice.dto.StockPriceDto;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class NasdaqBalticStockPriceScraper extends XlsxScraper<StockPriceDto>  {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String ISIN_PARAMETER = "isin";

    private static final String EX_DIVIDEND_DATE_PARAMETER = "exDividendDate";

    private final StockPriceProperties stockPriceProperties;

    public Optional<StockPriceDto> scrapeStockPrice(String stockIsin, LocalDate exDividendDate) {
        LOG.info("getting stock price for {} at {}", stockIsin, exDividendDate);
        setParameter(ISIN_PARAMETER, stockIsin);
        setParameter(EX_DIVIDEND_DATE_PARAMETER, exDividendDate);
        List<StockPriceDto> result = processAllRows();

        StockPriceDto stockPriceDto = null;
        if (!CollectionUtils.isEmpty(result)) {
            stockPriceDto = result.get(0);
        }

        return Optional.ofNullable(stockPriceDto);
    }

    @Override
    protected String getEndpoint() {
        String endpoint = stockPriceProperties.getEndpoint().replace("{ISIN}", (String) getParameter(ISIN_PARAMETER));
        String date = ((LocalDate) getParameter(EX_DIVIDEND_DATE_PARAMETER)).format(DateTimeFormatter.ISO_DATE);

        return UriComponentsBuilder.fromHttpUrl(endpoint)
                .queryParam("start", date)
                .queryParam("end", date)
                .toUriString();
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    protected Optional<StockPriceDto> processRow(Row row) {
        try {
            return Optional.ofNullable(getStockPriceFromRow(row));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    private StockPriceDto getStockPriceFromRow(Row row) {
        if (row.getCell(Header.AVERAGE.ordinal()) == null) {
            return null;
        }

        StockPriceDto stockPriceDto = StockPriceDto.builder()
                .average(BigDecimal.valueOf(row.getCell(Header.AVERAGE.ordinal()).getNumericCellValue()))
                .build();
        LOG.debug("parsed stock price successfully {}", stockPriceDto);
        return stockPriceDto;
    }

    enum Header {
        DATE,
        AVERAGE
    }

}
