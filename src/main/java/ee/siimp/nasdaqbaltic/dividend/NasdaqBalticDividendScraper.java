package ee.siimp.nasdaqbaltic.dividend;

import ee.siimp.nasdaqbaltic.common.utils.DateUtils;
import ee.siimp.nasdaqbaltic.stock.Stock;
import ee.siimp.nasdaqbaltic.stock.StockRepository;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class NasdaqBalticDividendScraper {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String URI_QUERY_PARAM_YEAR = "year";

    private static final ScriptEngine JAVASCRIPT_ENGINE = new ScriptEngineManager().getEngineByName("nashorn");

    private static final String DATA_JAVASCRIPT_ATTRIBUTE = "data";

    private static final String DATA_JAVASCRIPT_START = "var data = [{";

    private static final String DATA_JAVASCRIPT_END = "}]";

    private static final String DATA_TICKER = "ticker";

    private static final String DATA_AMOUNT = "amount";

    private static final String DATA_CURRENCY = "ccy";

    private static final String DATA_DATE = "starts";

    private static final String DATA_TYPE = "type";

    private static final String DATA_TYPE_CAPITAL_DECREASE = "capital-decrease";


    private final DividendProperties dividendProperties;

    private final StockRepository stockRepository;

    private final DividendRepository dividendRepository;

    private final EntityManager em;

    private final RestTemplate restTemplate;

    public void loadYearDividends(int year) throws ScriptException {
        URI endpoint = UriComponentsBuilder.fromHttpUrl(dividendProperties.getEndpoint())
                .queryParam(URI_QUERY_PARAM_YEAR, year).build().toUri();

        LOG.info("loading year {} dividends from {}", year, endpoint);

        String response = restTemplate.getForObject(endpoint, String.class);

        handleResponse(response);
    }

    private void handleResponse(String response) throws ScriptException {
        if (StringUtils.hasText(response)) {
            Optional<String> javaScriptDataValueOptional = getDataJavascriptValue(response);
            if (javaScriptDataValueOptional.isPresent()) {
                getDividendInfo(javaScriptDataValueOptional.get(), (ticker, amount, exDividendDate, currency, isCapitalDecrease) ->
                        stockRepository.findIdByTicker(ticker).ifPresent(stockId ->
                                saveNewDividend(amount, exDividendDate, currency, stockId, isCapitalDecrease, ticker)
                        )
                );
            }
        }
    }

    private void saveNewDividend(BigDecimal amount, LocalDate exDividendDate, String currency,
                                 Long stockId, boolean isCapitalDecrease, String ticker) {
        if (!dividendRepository.existsByStockIdAndExDividendDate(stockId, exDividendDate)) {
            LOG.info("saving dividend for {} with amount {} on {}", ticker, amount, exDividendDate);
            Dividend dividend = new Dividend();
            dividend.setAmount(amount);
            dividend.setExDividendDate(exDividendDate);
            dividend.setStock(em.getReference(Stock.class, stockId));
            dividend.setCurrency(currency);
            dividend.setCapitalDecrease(isCapitalDecrease);
            dividendRepository.save(dividend);
        } else {
            if (isCapitalDecrease) {
                // in case of capital decrease input data has two seperate events for that (Dividend ex-date and Capital decrease ex-date)
                LOG.info("marking dividend for {} with amount {} on {} as capital decrease", ticker, amount, exDividendDate);
                Dividend dividend = dividendRepository.findByStockIdAndExDividendDate(stockId, exDividendDate);
                dividend.setCapitalDecrease(true);
                dividendRepository.save(dividend);
            } else {
                LOG.info("dividend for {} with stock id {} and ex-dividend date {} already exist. isCapitalDecrease = {}",
                        ticker, stockId, exDividendDate, isCapitalDecrease);
            }
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
                assertAndAcceptDividendInfo(consumer, value);
            });
        }
    }

    private void assertAndAcceptDividendInfo(DividendDataConsumer consumer, ScriptObjectMirror value) {
        String ticker = (String) value.get(DATA_TICKER);
        String amount = (String) value.get(DATA_AMOUNT);
        String date = (String) value.get(DATA_DATE);
        String currency = (String) value.get(DATA_CURRENCY);
        String type = (String) value.get(DATA_TYPE);

        if (Stream.of(ticker, amount, date, currency, type).allMatch(StringUtils::hasText)) {
            consumer.accept(ticker, new BigDecimal(amount), DateUtils.parseEstonianDate(date),
                    currency, DATA_TYPE_CAPITAL_DECREASE.equals(type));
        } else {
            LOG.error("one of the dividend values has no content ticker={}, amount={}, date={}, currency={}, type={}",
                    ticker, amount, date, currency, type);
        }
    }

    private Optional<String> getDataJavascriptValue(String response) {
        int startIndex = response.indexOf(DATA_JAVASCRIPT_START);

        if (startIndex < 0) {
            return Optional.empty();
        }

        int endIndex = response.indexOf(DATA_JAVASCRIPT_END, startIndex);
        return Optional.of(response.substring(startIndex, endIndex + DATA_JAVASCRIPT_END.length()));
    }

}

@FunctionalInterface
interface DividendDataConsumer {
    void accept(String ticker, BigDecimal amount, LocalDate exDividendDate, String currency, boolean isCapitalDecrease);
}
