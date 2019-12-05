package ee.siimp.dividendyields.stock;

import ee.siimp.dividendyields.common.XlsxScraper;
import ee.siimp.dividendyields.stock.dto.StockDto;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class NasdaqBalticStockListScraper extends XlsxScraper<StockDto> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final StockProperties stockProperties;

    List<StockDto> loadAllStocks() {
        LOG.info("loading all stocks");
        List<StockDto> result = processAllRows();
        LOG.info("finished loading all stocks, result size is {}", result.size());
        return result;
    }

    @Override
    public String getEndpoint() {
        return stockProperties.getEndpoint();
    }

    @Override
    protected Resource getStaticResource() {
        return stockProperties.getStaticList();
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    public Optional<StockDto> processRow(Row row) {
        return Optional.of(getNewStock(row));
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

    enum Header {
        TICKER,
        NAME,
        ISIN,
        CURRENCY,
        MARKET_PLACE,
        SEGMENT
    }
}