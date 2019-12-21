package ee.siimp.dividendyields.stock;

import ee.siimp.dividendyields.dividendyield.dto.DividendYieldDto;
import ee.siimp.dividendyields.stockinfo.StockInfoController;
import ee.siimp.dividendyields.stockinfo.dto.StockAndIsinDto;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @Query("select stock.id from #{#entityName} stock where ticker = ?1")
    Optional<Long> findIdByTicker(String ticker);

    <T> List<T> findAllBy(Class<T> type);

    <T> List<T> findAllByStockInfoIsNull(Class<T> type);

    @Query("select stock.name as name, stock.ticker as ticker, stock.isin as isin, " +
            "dividend.exDividendDate as exDividendDate, dividend.amount as dividendAmount, dividend.capitalDecrease as capitalDecrease, " +
            "stockPrice.price as stockPriceAtExDividend, " +
            "((dividend.amount/stockPrice.price) * 100) as dividendYield, " +
            "(dividend.amount * coalesce(stockInfo.numberOfSecurities, null)) as dividendCost " +
            "from #{#entityName} stock " +
            "inner join Dividend dividend ON dividend.stock = stock " +
            "inner join StockPrice stockPrice ON (stockPrice.stock = stock AND stockPrice.date = dividend.exDividendDate) " +
            "left join StockInfo stockInfo ON  (stockInfo.stock = stock) " +
            "where YEAR(dividend.exDividendDate) = ?1 " +
            "order by dividendYield desc")
    List<DividendYieldDto> findAllWithDividendYieldsByYear(Integer year);


    @Query("select stock.name as name, stock.ticker as ticker, stock.isin as isin, " +
            "dividend.exDividendDate as exDividendDate, dividend.amount as dividendAmount, dividend.capitalDecrease as capitalDecrease, " +
            "stockPrice.price as currentStockPrice, " +
            "((dividend.amount/stockPrice.price) * 100) as dividendYield, " +
            "((dividend.amount/yesterdayStockPrice.price) * 100) as yesterdaysDividendYield, " +
            "(dividend.amount * coalesce(stockInfo.numberOfSecurities, null)) as dividendCost " +
            "from #{#entityName} stock " +
            "inner join Dividend dividend ON dividend.stock = stock " +
            "inner join StockPrice stockPrice ON (stockPrice.stock = stock AND stockPrice.date = CURRENT_DATE) " +
            "left join StockPrice yesterdayStockPrice ON (yesterdayStockPrice.stock = stock AND yesterdayStockPrice.date = (CURRENT_DATE - 1)) " +
            "left join StockInfo stockInfo ON  (stockInfo.stock = stock) " +
            "where YEAR(dividend.exDividendDate) = YEAR(CURRENT_DATE) and " +
            "dividend.exDividendDate > CURRENT_DATE " +
            "order by dividendYield desc")
    List<DividendYieldDto> findAllWithFutureDividendYields();

    @CacheEvict(cacheNames = {StockController.CACHE_NAME, StockInfoController.CACHE_NAME}, allEntries = true)
    @Override
    <S extends Stock> S save(S entity);

}
