package ee.siimp.nasdaqbaltic.dividend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface DividendRepository extends JpaRepository<Dividend, Long> {

    boolean existsByStockIdAndExDividendDate(Long stockId, LocalDate exDividendDate);

    //limit is not supported by @Query
    @Query("select count(*) > 0 from #{#entityName} where YEAR(exDividendDate) = ?1")
    boolean existsByYear(int year);

    //TODO: use appropriate dto
    @Query("select dividend from #{#entityName} dividend " +
            "left join StockPrice stockPrice on (stockPrice.stock = dividend.stock and stockPrice.date = dividend.exDividendDate) " +
            "where stockPrice is null")
    List<Dividend> findDividendsWithoutStockPriceInfo();

}
