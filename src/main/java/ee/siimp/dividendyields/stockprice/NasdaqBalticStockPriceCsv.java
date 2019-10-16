package ee.siimp.dividendyields.stockprice;

import org.apache.commons.csv.CSVFormat;

class NasdaqBalticStockPriceCsv {

    private NasdaqBalticStockPriceCsv() {
    }

    private static final String[] CSV_HEADERS = {"Date", "Avg", "Open", "High", "Low", "Close", Header.LAST,
            "Adjustment Factor", "Last price adjusted", "Chg%", "Bid", "Ask", "Trades", "Volume", "Turnover", "CCY"};

    static final CSVFormat FORMAT = CSVFormat.TDF.withHeader(CSV_HEADERS).withFirstRecordAsHeader();

    static class Header {

        private Header() {
        }

        static final String LAST = "Last";

    }
}
