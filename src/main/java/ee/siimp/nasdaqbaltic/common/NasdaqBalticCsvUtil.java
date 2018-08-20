package ee.siimp.nasdaqbaltic.common;

import lombok.experimental.UtilityClass;
import org.apache.commons.csv.CSVFormat;

@UtilityClass
class NasdaqBalticCsvUtil {

    private static final String[] CSV_HEADERS = {Header.TICKER, Header.NAME, Header.ISIN, Header.CURRENCY,
            Header.MARKET_PLACE, Header.SEGMENT, "Average Price", "Open Price", "High Price", "Low Price",
            "Last close Price", "Last Price", "Price Change(%)", "Best bid", "Best ask", "Trades", "Volume", "Turnover"};

    static final CSVFormat FORMAT = CSVFormat.TDF.withHeader(NasdaqBalticCsvUtil.CSV_HEADERS).withFirstRecordAsHeader();

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
