package ee.siimp.dividendyields.stockinfo;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.List;

@CacheConfig(cacheNames = StockInfoController.CACHE_NAME)
@RestController
@RequestMapping("/stock-info")
@RequiredArgsConstructor
public class StockInfoController {

    public static final String CACHE_NAME = "stock-info";

    private final StockInfoRepository stockInfoRepository;

    @Cacheable
    @GetMapping
    public List<StockInfoDto> get() {
        return stockInfoRepository.findAllBy(StockInfoDto.class);
    }
}

@Value
class StockInfoDto {
    String stockName;
    BigInteger numberOfSecurities;
}
