package ee.siimp.nasdaqbaltic.stockprice;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {

    @CacheEvict(cacheNames = "dividend-yield", allEntries = true)
    @Override
    <S extends StockPrice> S save(S entity);
}
