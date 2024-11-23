package backend.academy.logFieldFilters;

import backend.academy.model.LogRecord;

public class StatusFilter implements LogFieldFilter {
    @Override
    public boolean filter(LogRecord logRecord, String value) {
        return String.valueOf(logRecord.status()).equals(value);
    }
}
