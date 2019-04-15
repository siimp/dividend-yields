package ee.siimp.nasdaqbaltic.stockinfo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface StockInfoRepository extends JpaRepository<StockInfo, Long> {
    Optional<StockInfo> findByStockIdAndNumberOfSecurities(Long stockId, BigInteger numberOfSecurities);
}
