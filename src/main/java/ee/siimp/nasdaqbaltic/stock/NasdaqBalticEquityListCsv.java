package ee.siimp.nasdaqbaltic.stock;

import org.apache.commons.csv.CSVFormat;

public class NasdaqBalticEquityListCsv {

    private static final String[] CSV_HEADERS = {Header.TICKER, Header.NAME, Header.ISIN, Header.CURRENCY, Header.MARKET_PLACE, Header.SEGMENT,
            "Average Price", "Open Price", "High Price", "Low Price", "Last close Price", "Last Price", "Price Change(%)", "Best bid", "Best ask", "Trades", "Volume", "Turnover"};

    public static final CSVFormat FORMAT = CSVFormat.TDF.withHeader(CSV_HEADERS).withFirstRecordAsHeader();

    private NasdaqBalticEquityListCsv() {
    }

    public static class Header {

        private Header() {
        }

        public static final String TICKER = "Ticker";

        public static final String NAME = "Name";

        public static final String ISIN = "ISIN";

        public static final String CURRENCY = "Currency";

        public static final String MARKET_PLACE = "MarketPlace";

        public static final String SEGMENT = "List/segment";

    }
}
