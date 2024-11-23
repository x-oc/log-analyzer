package backend.academy.logFieldFilters;

import backend.academy.model.LogRecord;

public class BytesFilter implements LogFieldFilter {
    @Override
    public boolean filter(LogRecord logRecord, String value) {
        return String.valueOf(logRecord.bodyBytesSent()).equals(value);
    }
}
