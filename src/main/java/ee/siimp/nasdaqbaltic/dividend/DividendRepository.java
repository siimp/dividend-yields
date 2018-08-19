package ee.siimp.nasdaqbaltic.dividend;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DividendRepository extends JpaRepository<Dividend, Long> {
}
