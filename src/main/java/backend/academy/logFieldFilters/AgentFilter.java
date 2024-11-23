package backend.academy.logFieldFilters;

import backend.academy.model.LogRecord;

public class AgentFilter implements LogFieldFilter {
    @Override
    public boolean filter(LogRecord logRecord, String value) {
        return logRecord.httpUserAgent().contains(value);
    }
}

