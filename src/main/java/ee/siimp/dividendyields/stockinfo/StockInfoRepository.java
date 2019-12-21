package ee.siimp.dividendyields.stockinfo;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockInfoRepository extends JpaRepository<StockInfo, Long> {

    @CacheEvict(cacheNames = {StockInfoController.CACHE_NAME}, allEntries = true)
    @Override
    <S extends StockInfo> S save(S entity);

    <T> List<T> findAllBy(Class<T> type);

}
