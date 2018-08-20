package ee.siimp.nasdaqbaltic.dividend;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface DividendRepository extends JpaRepository<Dividend, Long> {
    boolean existsByStockIdAndExDividendDate(Long stockId, LocalDate exDividendDate);
}
