package ee.siimp.nasdaqbaltic.stockprice;

import ee.siimp.nasdaqbaltic.dividendyield.DividendYieldController;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {

    @CacheEvict(cacheNames = DividendYieldController.CACHE_NAME, allEntries = true)
    @Override
    <S extends StockPrice> S save(S entity);

    Optional<StockPrice> findByStockIdAndDate(Long stockId, LocalDate date);
}
