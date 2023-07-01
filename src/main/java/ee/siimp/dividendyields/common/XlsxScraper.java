package ee.siimp.dividendyields.common;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class XlsxScraper<T> {

    @Autowired
    private RestTemplate restTemplate;

    private Map<String, Object> parameters = new ConcurrentHashMap<>();

    protected abstract String getEndpoint();

    protected abstract Logger getLogger();

    protected Resource getStaticResource() {
        return null;
    }

    protected List<T> processAllRows() {
        List<T> result = new ArrayList<>();

        try {
            Iterator<Row> rows = getXslsSheet().rowIterator();
            rows.next(); // skip header
            rows.forEachRemaining((Row row) -> {
                Optional<T> optionalObject = processRow(row);
                optionalObject.ifPresent(result::add);
            });
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
            return Collections.emptyList();
        }

        return result;
    }

    protected abstract Optional<T> processRow(Row row);

    private Sheet getXslsSheet() throws IOException {
        try (Workbook workbook = WorkbookFactory.create(getXslsInputStream())) {
            return workbook.getSheetAt(0);
        }
    }

    private InputStream getXslsInputStream() throws IOException {
        if (getStaticResource() != null) {
            getLogger().debug("loading static file {}", getStaticResource().getFilename());
            return getStaticResource().getInputStream();
        } else {
            getLogger().debug("loading remote file from {}", getEndpoint());
            byte[] response = restTemplate.getForObject(getEndpoint(), byte[].class);
            return new ByteArrayInputStream(response);
        }
    }

    protected void setParameter(String key, Object value) {
        parameters.put(key, value);
    }

    protected Object getParameter(String key) {
        return parameters.get(key);
    }


}
