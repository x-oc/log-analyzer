package backend.academy.logFieldFilters;

import backend.academy.model.LogRecord;

public interface LogFieldFilter {
    boolean filter(LogRecord record, String value);
}
