package backend.academy;

import backend.academy.logFieldFilters.FieldFilterFactory;
import backend.academy.logFieldFilters.LogFieldFilter;
import backend.academy.model.LogRecord;
import backend.academy.model.LogReport;
import backend.academy.model.Metrics;
import backend.academy.model.TotalLogStats;
import com.google.common.math.Quantiles;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.util.Map.Entry;

public class LogAnalyzer {

    private final static int THE_95TH_PERCENTILE = 95;
    private final static int RESOURCES_COUNT = 5;
    private final static int CODES_COUNT = 5;
    private final static int REMOTE_USERS_COUNT = 5;
    private final static int HTTP_REFERRERS_COUNT = 5;
    private final LogParser parser;

    public LogAnalyzer(LogParser parser) {
        this.parser = parser;
    }

    public LogReport analyzeLogFiles(String path, LocalDateTime from, LocalDateTime to) throws IOException {
        return analyzeLogFiles(path, from, to, null, null);
    }

    public LogReport analyzeLogFiles(String path, LocalDateTime from, LocalDateTime to,
        String filterField, String filterValue) throws IOException {

        try (LogReader reader = new LogReader(path)) {
            Stream<String> lines = reader.read();

            TotalLogStats stats = new TotalLogStats();

            lines
                .map(parser::parseLine)
                .filter(logRecord -> getFiltered(filterField, filterValue, logRecord, from, to))
                .forEach(logRecord -> updateStats(stats, logRecord));

            return getReport(reader, path, stats, from, to);
        }
    }

    private boolean getFiltered(String filterField, String filterValue, LogRecord logRecord,
        LocalDateTime from, LocalDateTime to) {
        if (filterField == null || filterValue == null
            || filterValue.trim().isEmpty() || filterField.trim().isEmpty()) {
            return true;
        }
        boolean isNotBefore = from == null || logRecord.timeLocal().isAfter(from);
        boolean isNotAfter = to == null || logRecord.timeLocal().isBefore(to);
        LogFieldFilter fieldFilter = FieldFilterFactory.resolveFilter(filterField);

        return fieldFilter.filter(logRecord, filterValue) && isNotBefore && isNotAfter;
    }

    private void updateStats(TotalLogStats stats, LogRecord logRecord) {
        stats.totalRequests().getAndIncrement();
        stats.totalResponseSize().addAndGet(logRecord.bodyBytesSent());
        stats.responseSizes().add(logRecord.bodyBytesSent());
        stats.resources().put(logRecord.request().resource(),
            stats.resources().getOrDefault(logRecord.request().resource(), 0L) + 1);
        stats.responseCodes().put(String.valueOf(logRecord.status()),
            stats.responseCodes().getOrDefault(String.valueOf(logRecord.status()), 0L) + 1);
        stats.remoteUsers().put(logRecord.remoteUser(),
            stats.remoteUsers().getOrDefault(logRecord.remoteUser(), 0L) + 1);
        stats.httpReferrers().put(logRecord.httpReferer(),
            stats.httpReferrers().getOrDefault(logRecord.httpReferer(), 0L) + 1);
    }

    private LogReport getReport(LogReader reader, String path,
        TotalLogStats stats, LocalDateTime from, LocalDateTime to) {

        Metrics metrics = getMetrics(reader, path, stats, from, to);
        Map<String, String> resultResources = mapLongToString(mapTopN(stats.resources(), RESOURCES_COUNT));
        Map<String, String> resultCodes = mapLongToString(mapTopN(stats.responseCodes(), CODES_COUNT));
        Map<String, String> resultRemoteUsers = mapLongToString(mapTopN(stats.remoteUsers(), REMOTE_USERS_COUNT));
        Map<String, String> resultHttpReferrers = mapLongToString(mapTopN(stats.httpReferrers(), HTTP_REFERRERS_COUNT));

        return new LogReport(metrics, resultResources, resultCodes, resultRemoteUsers, resultHttpReferrers);
    }

    private Metrics getMetrics(LogReader reader, String path,
        TotalLogStats stats, LocalDateTime from, LocalDateTime to) {

        Metrics metrics = new Metrics();

        long response95p;
        try {
            response95p = (long) Quantiles.percentiles().index(THE_95TH_PERCENTILE).compute(stats.responseSizes());
        } catch (Exception e) {
            response95p = 0;
        }

        metrics.setMetric(Metrics.Metric.FILES, reader.getFileName(path));
        metrics.setMetric(Metrics.Metric.START_DATE, from == null ? "-" : from.toString());
        metrics.setMetric(Metrics.Metric.END_DATE, to == null ? "-" : to.toString());
        metrics.setMetric(Metrics.Metric.REQUEST_COUNT, stats.totalRequests().toString());
        metrics.setMetric(Metrics.Metric.AVERAGE_RESPONSE_SIZE,
            stats.totalResponseSize().get() / Math.max(stats.totalRequests().get(), 1) + "b");
        metrics.setMetric(Metrics.Metric.RESPONSE_95TH_PERCENTILE, response95p + "b");

        return metrics;
    }

    private Map<String, Long> mapTopN(Map<String, Long> map, int n) {
        List<Entry<String, Long>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByValue());
        Collections.reverse(list);

        Map<String, Long> result = new LinkedHashMap<>(n);
        for (Entry<String, Long> entry : list.subList(0, Math.min(n, list.size()))) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    private Map<String, String> mapLongToString(Map<String, Long> map) {
        return map.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> String.valueOf(e.getValue())));
    }
}
