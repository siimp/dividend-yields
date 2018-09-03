package ee.siimp.nasdaqbaltic.stockdividend;

import ee.siimp.nasdaqbaltic.stock.StockRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping("/stock-dividend")
@AllArgsConstructor
public class StockDividendController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private StockDividendService stockDividendService;
    private StockRepository stockRepository;

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, path = "/")
    public List<StockDividendResultDto> get() {
        LOG.info("getting stock dividends");
        return stockDividendService.getAll();
    }

}
