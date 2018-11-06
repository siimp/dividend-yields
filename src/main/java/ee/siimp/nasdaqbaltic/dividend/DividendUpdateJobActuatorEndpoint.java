package ee.siimp.nasdaqbaltic.dividend;

import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "dividend-update-job")
@AllArgsConstructor
public class DividendUpdateJobActuatorEndpoint {

    private DividendUpdateJob dividendUpdateJob;

    @ReadOperation
    public ResponseEntity executeJob() {
        dividendUpdateJob.execute();
        return ResponseEntity.ok().build();
    }

}
