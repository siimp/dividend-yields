package ee.siimp.nasdaqbaltic.dividendyield;

import ee.siimp.nasdaqbaltic.dividendyield.dto.DividendYieldResultDto;
import ee.siimp.nasdaqbaltic.stock.StockRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
@AllArgsConstructor
public class DividendYieldService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private StockRepository stockRepository;

    public List<DividendYieldResultDto> getByYear(Integer year) {
        LOG.info("getByYear {}", year);
        List<DividendYieldResultDto> result = stockRepository.findAllWithDividendYieldsByYear(year);
        LOG.info("result size is {}", result.size());
        return result;
    }

    public List<DividendYieldResultDto> getFutureYields() {
        LOG.info("getting this year current possible divided yields");
        List<DividendYieldResultDto> result = stockRepository.findAllWithFutureDividendYields();
        LOG.info("result size is {}", result.size());
        return result;
    }
}
