package backend.academy.logFieldFilters;

import backend.academy.model.LogRecord;

public class RequestFilter implements LogFieldFilter {
    @Override
    public boolean filter(LogRecord logRecord, String value) {
        return logRecord.request().method().contains(value) ||
            logRecord.request().resource().contains(value) ||
            logRecord.request().protocol().contains(value);
    }
}
