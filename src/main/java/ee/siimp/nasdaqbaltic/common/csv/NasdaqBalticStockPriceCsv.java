package ee.siimp.nasdaqbaltic.common.csv;

import org.apache.commons.csv.CSVFormat;

public class NasdaqBalticStockPriceCsv {

    private NasdaqBalticStockPriceCsv() {
    }

    private static final String[] CSV_HEADERS = {"Date", "Avg", "Open", "High", "Low", "Close", Header.LAST,
            "Adjustment Factor", "Last price adjusted", "Chg%", "Bid", "Ask", "Trades", "Volume", "Turnover", "CCY"};

    public static final CSVFormat FORMAT = CSVFormat.TDF.withHeader(CSV_HEADERS).withFirstRecordAsHeader();

    public static class Header {

        private Header() {
        }

        public static final String LAST = "Last";

    }
}
