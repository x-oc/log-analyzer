package backend.academy.logFieldFilters;

import backend.academy.model.LogRecord;

public class ReferrerFilter implements LogFieldFilter {
    @Override
    public boolean filter(LogRecord logRecord, String value) {
        return logRecord.httpReferer().contains(value);
    }
}
