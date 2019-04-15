package ee.siimp.nasdaqbaltic.stock;

import ee.siimp.nasdaqbaltic.dividendyield.dto.DividendYieldRepositoryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @Query("select stock.id from #{#entityName} stock where ticker = ?1")
    Optional<Long> findIdByTicker(String ticker);

    <T> Collection<T> findAllBy(Class<T> type);

    @Query("select stock.name as name, stock.ticker as ticker, stock.isin as isin, " +
            "dividend.exDividendDate as exDividendDate, dividend.amount as dividendAmount, dividend.capitalDecrease as capitalDecrease, " +
            "stockPrice.price as stockPriceAtExDividend, " +
            "((dividend.amount/stockPrice.price) * 100) as dividendYield " +
            "from #{#entityName} stock " +
            "inner join Dividend dividend ON dividend.stock = stock " +
            "inner join StockPrice stockPrice ON (stockPrice.stock = stock AND stockPrice.date = dividend.exDividendDate) " +
            "where YEAR(dividend.exDividendDate) = ?1 " +
            "order by dividendYield desc")
    List<DividendYieldRepositoryDto> findAllWithDividendYieldsByYear(Integer year);


    @Query("select stock.name as name, stock.ticker as ticker, stock.isin as isin, " +
            "dividend.exDividendDate as exDividendDate, dividend.amount as dividendAmount, dividend.capitalDecrease as capitalDecrease, " +
            "stockPrice.price as currentStockPrice, " +
            "((dividend.amount/stockPrice.price) * 100) as dividendYield, " +
            "((dividend.amount/yesterdayStockPrice.price) * 100) as yesterdaysDividendYield " +
            "from #{#entityName} stock " +
            "inner join Dividend dividend ON dividend.stock = stock " +
            "inner join StockPrice stockPrice ON (stockPrice.stock = stock AND stockPrice.date = CURRENT_DATE) " +
            "left join StockPrice yesterdayStockPrice ON (yesterdayStockPrice.stock = stock AND yesterdayStockPrice.date = (CURRENT_DATE - 1)) " +
            "where YEAR(dividend.exDividendDate) = YEAR(CURRENT_DATE) and " +
            "dividend.exDividendDate > CURRENT_DATE " +
            "order by dividendYield desc")
    List<DividendYieldRepositoryDto> findAllWithFutureDividendYields();
}
