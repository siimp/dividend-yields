package ee.siimp.nasdaqbaltic.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @Query("select s.id from #{#entityName} s where ticker = ?1")
    Optional<Long> findIdByTicker(String ticker);
}
