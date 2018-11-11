package ee.siimp.nasdaqbaltic.stockprice;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "stock-price-job")
@RequiredArgsConstructor
public class StockPriceJobActuatorEndpoint {

    private final StockPriceJob stockPriceJob;

    @ReadOperation
    public ResponseEntity executeJob() {
        stockPriceJob.execute();
        return ResponseEntity.ok().build();
    }
}
