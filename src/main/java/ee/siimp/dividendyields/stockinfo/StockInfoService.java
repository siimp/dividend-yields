package ee.siimp.dividendyields.stockinfo;

import ee.siimp.dividendyields.common.utils.ThreadUtils;
import ee.siimp.dividendyields.dividendyield.DividendYieldController;
import ee.siimp.dividendyields.stock.StockRepository;
import ee.siimp.dividendyields.stockinfo.dto.StockAndIsinDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
@RequiredArgsConstructor
class StockInfoService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final StockRepository stockRepository;

    private final NasdaqBalticStockInfoScraper nasdaqBalticStockInfoScraper;

    private final CacheManager cacheManager;

    void collectStockInfo() {
        LOG.info("collecting stock info");
        for (StockAndIsinDto stock : stockRepository.findAllBy(StockAndIsinDto.class)) {
            nasdaqBalticStockInfoScraper.loadStockInfo(stock);
            ThreadUtils.randomSleep();
        }
        cacheManager.getCache(DividendYieldController.CACHE_NAME).clear();
    }


}
