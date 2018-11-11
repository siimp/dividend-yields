package ee.siimp.nasdaqbaltic.dividendyield;

import ee.siimp.nasdaqbaltic.dividendyield.dto.DividendYieldRepositoryDto;
import ee.siimp.nasdaqbaltic.dividendyield.dto.DividendYieldResultDto;
import ee.siimp.nasdaqbaltic.stock.StockRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DividendYieldService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final StockRepository stockRepository;

    public List<DividendYieldResultDto> getByYear(Integer year) {
        LOG.info("getByYear {}", year);
        List<DividendYieldRepositoryDto> searchResult = stockRepository.findAllWithDividendYieldsByYear(year);
        List<DividendYieldResultDto> result = getDtoResult(searchResult);
        LOG.info("result size is {}", result.size());
        return result;
    }

    public List<DividendYieldResultDto> getFutureYields() {
        LOG.info("getting this year current possible divided yields");
        List<DividendYieldRepositoryDto> searchResult = stockRepository.findAllWithFutureDividendYields();
        List<DividendYieldResultDto> result = getDtoResult(searchResult);
        LOG.info("result size is {}", result.size());
        return result;
    }

    private List<DividendYieldResultDto> getDtoResult(List<DividendYieldRepositoryDto> searchResult) {
        Map<String, DividendYieldResultDto> result = new HashMap<>();
        for (DividendYieldRepositoryDto dto : searchResult) {
            if (result.containsKey(dto.getTicker())) {
                result.get(dto.getTicker()).add(dto);
            } else {
                result.put(dto.getTicker(), DividendYieldResultDto.of(dto));
            }
        }

        return result.values().stream()
                .sorted(Comparator.comparing(DividendYieldResultDto::getTotalDividendYield).reversed())
                .collect(Collectors.toList());
    }
}
