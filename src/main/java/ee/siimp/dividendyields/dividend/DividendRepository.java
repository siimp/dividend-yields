package ee.siimp.dividendyields.dividend;

import ee.siimp.dividendyields.dividend.dto.DividendStockPriceDto;
import ee.siimp.dividendyields.dividendyield.DividendYieldController;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DividendRepository extends JpaRepository<Dividend, Long> {

    @Query("select stock.id as stockId, stock.isin as stockIsin, dividend.exDividendDate as exDividendDate " +
            "from #{#entityName} dividend " +
            "inner join dividend.stock stock " +
            "left join StockPrice stockPrice on (stockPrice.stock = dividend.stock and stockPrice.date = dividend.exDividendDate) " +
            "where stockPrice is null AND dividend.exDividendDate < CURRENT_DATE")
    List<DividendStockPriceDto> findPastDividendsWithoutStockPriceInfo();

    @Query("select stock.id as stockId, stock.isin as stockIsin, dividend.exDividendDate as exDividendDate " +
            "from #{#entityName} dividend " +
            "inner join dividend.stock stock " +
            "left join StockPrice stockPrice on (stockPrice.stock = dividend.stock and stockPrice.date = CURRENT_DATE) " +
            "where stockPrice is null AND dividend.exDividendDate >= CURRENT_DATE")
    List<DividendStockPriceDto> findFutureDividendsWithoutStockPriceInfo();

    @CacheEvict(cacheNames = {DividendYieldController.CACHE_NAME, DividendController.CACHE_NAME}, allEntries = true)
    @Override
    <S extends Dividend> S save(S entity);

    <T> List<T> findAllBy(Class<T> type);
}
