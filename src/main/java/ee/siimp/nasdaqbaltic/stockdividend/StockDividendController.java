package ee.siimp.nasdaqbaltic.stockdividend;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/stock-dividend")
public class StockDividendController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, path = "/")
    public Flux<StockDividendResultDto> get() {
        return Flux.empty();
    }

}
