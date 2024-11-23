package backend.academy.logFieldFilters;

import backend.academy.model.LogRecord;

public interface LogFieldFilter {
    boolean filter(LogRecord logRecord, String value);
}
