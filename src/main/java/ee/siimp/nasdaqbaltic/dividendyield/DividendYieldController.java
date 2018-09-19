package ee.siimp.nasdaqbaltic.dividendyield;

import ee.siimp.nasdaqbaltic.dividendyield.dto.DividendYieldResultDto;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping("/dividend-yield")
@AllArgsConstructor
public class DividendYieldController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private DividendYieldService dividendYieldService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<DividendYieldResultDto> getDividendYieldByYear(@RequestParam(name = "year") Integer year) {
        LOG.info("getting dividend yield for wear {}", year);
        return dividendYieldService.getByYear(year);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, path = "/future")
    public List<DividendYieldResultDto> getThisYearFutureDividendYield() {
        LOG.info("getting this year future dividend yield");
        return dividendYieldService.getFutureYields();
    }
}
