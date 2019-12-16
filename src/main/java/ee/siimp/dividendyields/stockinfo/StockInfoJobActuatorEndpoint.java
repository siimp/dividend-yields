package ee.siimp.dividendyields.stockinfo;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "stock-info-job")
@RequiredArgsConstructor
public class StockInfoJobActuatorEndpoint {

    private final StockInfoJob stockInfoJob;

    @ReadOperation
    public ResponseEntity executeJob() {
        stockInfoJob.execute();
        return ResponseEntity.ok().build();
    }
}
