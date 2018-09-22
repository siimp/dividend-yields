package ee.siimp.nasdaqbaltic.stock;

import ee.siimp.nasdaqbaltic.dividendyield.dto.DividendYieldResultDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @Query("select stock.id from #{#entityName} stock where ticker = ?1")
    Optional<Long> findIdByTicker(String ticker);

    @Query("select stock.name as name, stock.ticker as ticker, stock.isin as isin, " +
            "dividend.exDividendDate as exDividendDate, dividend.amount as dividendAmount, " +
            "stockPrice.price as stockPriceAtExDividend, " +
            "((dividend.amount/stockPrice.price) * 100) as dividendYield " +
            "from #{#entityName} stock " +
            "inner join Dividend dividend ON dividend.stock = stock " +
            "inner join StockPrice stockPrice ON (stockPrice.stock = stock AND stockPrice.date = dividend.exDividendDate) " +
            "where YEAR(dividend.exDividendDate) = ?1 " +
            "order by dividendYield desc")
    List<DividendYieldResultDto> findAllWithDividendYieldsByYear(Integer year);


    @Query("select stock.name as name, stock.ticker as ticker, stock.isin as isin, " +
            "dividend.exDividendDate as exDividendDate, dividend.amount as dividendAmount, " +
            "stockPrice.price as stockPriceAtExDividend, " +
            "((dividend.amount/stockPrice.price) * 100) as dividendYield " +
            "from #{#entityName} stock " +
            "inner join Dividend dividend ON dividend.stock = stock " +
            "inner join StockPrice stockPrice ON (stockPrice.stock = stock AND stockPrice.date = CURRENT_DATE) " +
            "where YEAR(dividend.exDividendDate) = YEAR(CURRENT_DATE) and " +
            "dividend.exDividendDate > CURRENT_DATE " +
            "order by dividendYield desc")
    List<DividendYieldResultDto> findAllWithFutureDividendYields();
}
