package ee.siimp.dividendyields.stock;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CacheConfig(cacheNames = StockController.CACHE_NAME)
@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

    public static final String CACHE_NAME = "stock";

    private final StockRepository stockRepository;

    @Cacheable
    @GetMapping
    public List<StockDto> get() {
        return stockRepository.findAllBy(StockDto.class);
    }
}

@Value
class StockDto {
    String name;
    String ticker;
    String segment;
}
