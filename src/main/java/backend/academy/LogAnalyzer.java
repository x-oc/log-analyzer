package backend.academy;

import com.google.common.math.Quantiles;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogAnalyzer {

    private final LogParser parser;

    public LogAnalyzer() {
        parser = new LogParser();
    }

    public Analysis analyzeLogFiles(String path, LocalDateTime from, LocalDateTime to) throws IOException {

        try (LogReader reader = new LogReader(path)) {
            Stream<String> lines = reader.read();

            Map<String, Long> resources = new HashMap<>();
            Map<String, Long> responseCodes = new HashMap<>();
            List<Long> responseSizes = new ArrayList<>();
            AtomicLong totalRequests = new AtomicLong();
            AtomicLong totalResponseSize = new AtomicLong();

            lines
                .map(parser::parseLine)
                .filter(record -> {
                    boolean isNotBefore = from == null || record.timeLocal().isAfter(from.minusSeconds(1));
                    boolean isNotAfter = to == null || record.timeLocal().isBefore(to.plusSeconds(1));
                    return isNotBefore && isNotAfter;
                })
                .forEach(record -> {
                    totalRequests.getAndIncrement();
                    totalResponseSize.addAndGet(record.bodyBytesSent());
                    responseSizes.add(record.bodyBytesSent());
                    resources.put(record.request().split("\\s+")[1],
                        resources.getOrDefault(record.request().split("\\s+")[1], 0L) + 1);
                    responseCodes.put(String.valueOf(record.status()),
                        responseCodes.getOrDefault(String.valueOf(record.status()), 0L) + 1);
                });

            long response95p = (long) Quantiles.percentiles().index(95).compute(responseSizes);
            Map<String, String> metrics = new HashMap<>();
            metrics.put("Количество запросов", totalRequests.toString());
            metrics.put("Средний размер ответа", totalResponseSize.get() / totalRequests.get() + "b");
            metrics.put("95p размера ответа", response95p + "b");
            metrics.put("Начальная дата", from == null ? "-" : from.toString());
            metrics.put("Конечная дата", to == null ? "-" : to.toString());
            metrics.put("Файл(-ы)", getFileName(path));

            return new Analysis(metrics, mapLongToString(resources), mapLongToString(responseCodes));
        }
    }

    private String getFileName(String path) {
        File file = new File(path);
        StringBuilder name = new StringBuilder();

        if (file.isFile()) {
            name.append(file.getName());
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    name.append(f.getName()).append(" ");
                }
            }
        }
        return name.toString();
    }

    private Map<String, String> mapLongToString(Map<String, Long> map) {
        return map.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> String.valueOf(e.getValue())));
    }
}
