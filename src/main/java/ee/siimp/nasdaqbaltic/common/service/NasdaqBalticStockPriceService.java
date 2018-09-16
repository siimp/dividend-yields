package ee.siimp.nasdaqbaltic.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.invoke.MethodHandles;

@Service
public class NasdaqBalticStockPriceService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    //&instrument=LV0000100659&start=01.09.2000&end=01.09.2000
    private static final String URI_QUERY_PARAM_INSTRUMENT = "instrument";
    private static final String URI_QUERY_PARAM_DATE_START = "start";
    private static final String URI_QUERY_PARAM_DATE_END = "end";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${nasdaqbaltic.stock-price-endpoint}")
    private String nasdaqBalticStockPriceEndpoint;

}
