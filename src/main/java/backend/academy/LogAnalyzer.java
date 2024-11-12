package backend.academy;

import com.google.common.math.Quantiles;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.util.Map.Entry;

public class LogAnalyzer {

    private final LogParser parser;
    private final static int P95 = 95;
    private final static int RESOURCES_COUNT = 5;
    private final static int CODES_COUNT = 5;

    public LogAnalyzer() {
        parser = new LogParser();
    }

    public LogReport analyzeLogFiles(String path, LocalDateTime from, LocalDateTime to) throws IOException {

        try (LogReader reader = new LogReader(path)) {
            Stream<String> lines = reader.read();

            Map<String, Long> resources = new HashMap<>();
            Map<String, Long> responseCodes = new HashMap<>();
            List<Long> responseSizes = new ArrayList<>();
            AtomicLong totalRequests = new AtomicLong();
            AtomicLong totalResponseSize = new AtomicLong();

            lines
                .map(parser::parseLine)
                .filter(logRecord -> {
                    boolean isNotBefore = from == null || logRecord.timeLocal().isAfter(from.minusSeconds(1));
                    boolean isNotAfter = to == null || logRecord.timeLocal().isBefore(to.plusSeconds(1));
                    return isNotBefore && isNotAfter;
                })
                .forEach(logRecord -> {
                    totalRequests.getAndIncrement();
                    totalResponseSize.addAndGet(logRecord.bodyBytesSent());
                    responseSizes.add(logRecord.bodyBytesSent());
                    resources.put(logRecord.request().split(" ")[1],
                        resources.getOrDefault(logRecord.request().split(" ")[1], 0L) + 1);
                    responseCodes.put(String.valueOf(logRecord.status()),
                        responseCodes.getOrDefault(String.valueOf(logRecord.status()), 0L) + 1);
                });

            long response95p;
            try {
                response95p = (long) Quantiles.percentiles().index(P95).compute(responseSizes);
            } catch (Exception e) {
                response95p = 0;
            }
            Map<String, String> metrics = new LinkedHashMap<>();
            metrics.put("Файл(-ы)", reader.getFileName(path));
            metrics.put("Начальная дата", from == null ? "-" : from.toString());
            metrics.put("Конечная дата", to == null ? "-" : to.toString());
            metrics.put("Количество запросов", totalRequests.toString());
            metrics.put("Средний размер ответа", totalResponseSize.get() / Math.max(totalRequests.get(), 1) + "b");
            metrics.put("95p размера ответа", response95p + "b");

            Map<String, String> resultResources = mapLongToString(mapTopN(resources, RESOURCES_COUNT));
            Map<String, String> resultCodes = mapLongToString(mapTopN(responseCodes, CODES_COUNT));

            return new LogReport(metrics, resultResources, resultCodes);
        }
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
