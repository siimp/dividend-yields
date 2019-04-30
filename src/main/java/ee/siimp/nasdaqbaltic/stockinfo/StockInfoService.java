package ee.siimp.nasdaqbaltic.stockinfo;

import ee.siimp.nasdaqbaltic.common.utils.ThreadUtils;
import ee.siimp.nasdaqbaltic.dividendyield.DividendYieldController;
import ee.siimp.nasdaqbaltic.stock.StockRepository;
import ee.siimp.nasdaqbaltic.stockinfo.dto.StockAndIsinDto;
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
