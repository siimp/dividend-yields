package ee.siimp.dividendyields.stockinfo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockInfoRepository extends JpaRepository<StockInfo, Long> {

    boolean existsByStockId(Long stockId);

    Optional<StockInfo> findByStockId(Long stockId);

}
