package backend.academy.logFieldFilters;

import backend.academy.model.LogRecord;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TimeFilter implements LogFieldFilter {
    @Override
    public boolean filter(LogRecord logRecord, String value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
        LocalDateTime timeLocal = LocalDateTime.parse(value, formatter);
        return logRecord.timeLocal().equals(timeLocal);
    }
}
