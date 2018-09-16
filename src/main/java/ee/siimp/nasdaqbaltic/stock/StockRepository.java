package ee.siimp.nasdaqbaltic.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @Query("select stock.id from #{#entityName} stock where ticker = ?1")
    Optional<Long> findIdByTicker(String ticker);

    @Query("select stock from #{#entityName} stock " +
            "inner join Dividend dividend ON dividend.stock = stock " +
            "inner join StockPrice stockPrice ON (stockPrice.stock = stock AND stockPrice.date = dividend.exDividendDate)")
    List<Stock> findAllWithDividends();
}
