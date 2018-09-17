package ee.siimp.nasdaqbaltic.common.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateUtils {

    private static final DateTimeFormatter ESTONIAN_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public LocalDate parseEstonianDate(String dateString) {
        return LocalDate.parse(dateString, ESTONIAN_DATE_FORMATTER);
    }

    public static String formatEstonianDate(LocalDate date) {
        return date.format(ESTONIAN_DATE_FORMATTER);
    }
}
