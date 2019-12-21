package ee.siimp.dividendyields.stockinfo;

import ee.siimp.dividendyields.common.utils.ThreadUtils;
import ee.siimp.dividendyields.dividendyield.DividendYieldController;
import ee.siimp.dividendyields.stock.Stock;
import ee.siimp.dividendyields.stock.StockRepository;
import ee.siimp.dividendyields.stockinfo.dto.StockAndIsinDto;
import ee.siimp.dividendyields.stockinfo.dto.StockInfoDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class StockInfoService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final StockRepository stockRepository;

    private final NasdaqBalticStockInfoScraper nasdaqBalticStockInfoScraper;

    private final CacheManager cacheManager;

    void collectStockInfo() {
        LOG.info("collecting stock info");
        for (StockAndIsinDto stock : stockRepository.findAllByStockInfoIsNull(StockAndIsinDto.class)) {
            Optional<StockInfoDto> stockInfoDtoOptional = nasdaqBalticStockInfoScraper.scrapeStockInfo(stock);
            stockInfoDtoOptional.ifPresent(stockInfoDto -> saveStockInfo(stock, stockInfoDto));
            ThreadUtils.randomSleep();
        }
        cacheManager.getCache(DividendYieldController.CACHE_NAME).clear();
    }

    private void saveStockInfo(StockAndIsinDto stockAndIsinDto, StockInfoDto stockInfoDto) {
        StockInfo stockInfo = StockInfo.builder()
                .numberOfSecurities(stockInfoDto.getNumberOfSecurities())
                .build();
        LOG.info("saving new stock info {}", stockInfoDto);

        Optional<Stock> stockOptional = stockRepository.findById(stockAndIsinDto.getId());
        if (stockOptional.isPresent()) {
            Stock stock = stockOptional.get();
            stock.setStockInfo(stockInfo);
            stockRepository.save(stock);
        }
    }


}
