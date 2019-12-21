package ee.siimp.dividendyields.dividend;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@CacheConfig(cacheNames = DividendController.CACHE_NAME)
@RestController
@RequestMapping("/dividend")
@RequiredArgsConstructor
public class DividendController {

    public static final String CACHE_NAME = "dividend";

    private final DividendRepository dividendRepository;

    @Cacheable
    @GetMapping
    public List<DividendDto> get() {
        return dividendRepository.findAllBy(DividendDto.class);
    }
}

@Value
class DividendDto {
    String stockName;
    LocalDate exDividendDate;
    BigDecimal amount;
    boolean capitalDecrease;
}
