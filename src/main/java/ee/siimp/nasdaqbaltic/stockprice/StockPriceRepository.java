package ee.siimp.nasdaqbaltic.stockprice;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {


}
