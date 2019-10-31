package ee.siimp.dividendyields.common.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@UtilityClass
public class DateUtils {

    private static final DateTimeFormatter ESTONIAN_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public LocalDate parseEstonianDate(String dateString) {
        return LocalDate.parse(dateString, ESTONIAN_DATE_FORMATTER);
    }

    public static String formatEstonianDate(LocalDate date) {
        return date.format(ESTONIAN_DATE_FORMATTER);
    }

    public static LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
