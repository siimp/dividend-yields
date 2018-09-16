package ee.siimp.nasdaqbaltic.common.service;

import ee.siimp.nasdaqbaltic.dividend.Dividend;
import ee.siimp.nasdaqbaltic.dividend.DividendRepository;
import ee.siimp.nasdaqbaltic.stock.Stock;
import ee.siimp.nasdaqbaltic.stock.StockRepository;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@Transactional
public class NasdaqBalticDividendService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String URI_QUERY_PARAM_YEAR = "year";

    private static final ScriptEngine JAVASCRIPT_ENGINE = new ScriptEngineManager().getEngineByName("nashorn");

    private static final String DATA_JAVASCRIPT_ATTRIBUTE = "data";

    private static final String DATA_JAVASCRIPT_START = "var data = [{";

    private static final String DATA_JAVASCRIPT_END = "}]";

    private static final String DATA_TICKER = "ticker";

    private static final String DATA_AMOUNT = "amount";

    private static final String DATA_CURRENCY = "ccy";

    private static final DateTimeFormatter DATA_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final String DATA_DATE = "starts";

    @Value("${nasdaqbaltic.dividend-endpoint}")
    private String nasdaqBalticDividendEndpoint;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private DividendRepository dividendRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private RestTemplate restTemplate;

    public void loadYearDividends(int year) throws ScriptException {
        URI endpoint = UriComponentsBuilder.fromHttpUrl(nasdaqBalticDividendEndpoint)
                .queryParam(URI_QUERY_PARAM_YEAR, year).build().toUri();

        LOG.info("loading year {} dividends from {}", year, endpoint);

        String response = restTemplate.getForObject(endpoint, String.class);

        if (StringUtils.hasText(response)) {
            Optional<String> javaScriptDataValueOptional = getDataJavascriptValue(response);
            if (javaScriptDataValueOptional.isPresent()) {
                getDividendInfo(javaScriptDataValueOptional.get(), (ticker, amount, exDividendDate, currency) ->
                        stockRepository.findIdByTicker(ticker).ifPresent(stockId -> {
                            LOG.info("saving dividend for {} with amount {} on {}", ticker, amount, exDividendDate);
                            saveNewDividend(amount, exDividendDate, currency, stockId);
                        })
                );
            }
        }
    }

    private void saveNewDividend(BigDecimal amount, LocalDate exDividendDate, String currency, Long stockId) {
        if (!dividendRepository.existsByStockIdAndExDividendDate(stockId, exDividendDate)) {
            Dividend dividend = new Dividend();
            dividend.setAmount(amount);
            dividend.setExDividendDate(exDividendDate);
            dividend.setStock(em.getReference(Stock.class, stockId));
            dividend.setCurrency(currency);
            dividendRepository.save(dividend);
        }
    }

    private void getDividendInfo(String javaScriptDataValue, DividendDataConsumer consumer) throws ScriptException {
        ScriptContext ctx = new SimpleScriptContext();
        ctx.setBindings(JAVASCRIPT_ENGINE.createBindings(), ScriptContext.ENGINE_SCOPE);
        JAVASCRIPT_ENGINE.eval(javaScriptDataValue, ctx);
        ScriptObjectMirror data = (ScriptObjectMirror) ctx.getAttribute(DATA_JAVASCRIPT_ATTRIBUTE);

        if (data.isArray()) {
            data.forEach((k, v) -> {
                ScriptObjectMirror value = (ScriptObjectMirror) v;
                consumer.accept(
                        (String) value.get(DATA_TICKER),
                        new BigDecimal((String) value.get(DATA_AMOUNT)),
                        LocalDate.parse((String) value.get(DATA_DATE), DATA_DATE_FORMATTER),
                        (String) value.get(DATA_CURRENCY));
            });
        }
    }

    private Optional<String> getDataJavascriptValue(String response) {
        Integer startIndex = response.indexOf(DATA_JAVASCRIPT_START);

        if (startIndex < 0) {
            return Optional.empty();
        }

        Integer endIndex = response.indexOf(DATA_JAVASCRIPT_END, startIndex);
        return Optional.of(response.substring(startIndex, endIndex + DATA_JAVASCRIPT_END.length()));
    }

}

@FunctionalInterface
interface DividendDataConsumer {
    void accept(String ticker, BigDecimal amount, LocalDate exDividendDate, String currency);
}
