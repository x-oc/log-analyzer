package backend.academy.logFieldFilters;

import backend.academy.model.LogRecord;

public class UserFilter implements LogFieldFilter {
    @Override
    public boolean filter(LogRecord logRecord, String value) {
        return logRecord.remoteUser().contains(value);
    }
}
