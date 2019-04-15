package ee.siimp.nasdaqbaltic.stockinfo;

import ee.siimp.nasdaqbaltic.stock.StockRepository;
import ee.siimp.nasdaqbaltic.stockinfo.dto.StockIdIsinDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class StockInfoService {

    private final StockRepository stockRepository;

    private final NasdaqBalticStockInfoScraper nasdaqBalticStockInfoScraper;

    void collectStockInfo() {
        for (StockIdIsinDto stock : stockRepository.findAllBy(StockIdIsinDto.class)) {
            nasdaqBalticStockInfoScraper.loadStockInfo(stock.getId(), stock.getIsin());
        }
    }
}
