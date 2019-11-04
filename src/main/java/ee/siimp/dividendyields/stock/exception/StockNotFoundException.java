package ee.siimp.dividendyields.stock.exception;

public class StockNotFoundException extends RuntimeException {

    public StockNotFoundException(String ticker) {
        super("Stock not found by ticker " + ticker);
    }
}
