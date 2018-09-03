package ee.siimp.nasdaqbaltic.stockdividend;

import ee.siimp.nasdaqbaltic.stock.StockRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
@AllArgsConstructor
public class StockDividendService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private StockRepository stockRepository;

    public List<StockDividendResultDto> getAll() {

        return null;
    }
}
