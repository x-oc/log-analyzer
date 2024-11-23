package backend.academy.logFieldFilters;

import backend.academy.model.LogRecord;

public class AddressFilter implements LogFieldFilter {
    @Override
    public boolean filter(LogRecord logRecord, String value) {
        return logRecord.remoteAddr().contains(value);
    }
}
