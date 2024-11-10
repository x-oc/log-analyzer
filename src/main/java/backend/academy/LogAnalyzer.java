package backend.academy;

import com.google.common.math.Quantiles;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        Stream<String> lines;

        if (path.startsWith("https://") || path.startsWith("http://")) {
            lines = readUrl(path);
        } else {
            lines = readLocalFiles(path);
        }

        Map<String, Long> resources = new HashMap<>();
        Map<String, Long> responseCodes = new HashMap<>();
        List<Long> responseSizes = new ArrayList<>();
        AtomicLong totalRequests = new AtomicLong();
        AtomicLong totalResponseSize = new AtomicLong();

        //todo: Exception in thread "main" java.io.UncheckedIOException: java.io.IOException: Stream closed
        lines
            .map(parser::parseLine)
            .filter(record -> {
                boolean isNotBefore = from == null || record.timeLocal().isAfter(from.minusDays(1));
                boolean isNotAfter = to == null || record.timeLocal().isBefore(to.plusDays(1));
                return isNotBefore && isNotAfter;
            })
            .forEach(record -> {
                totalRequests.getAndIncrement();
                totalResponseSize.addAndGet(record.bodyBytesSent());
                responseSizes.add(record.bodyBytesSent());
                resources.put(record.request().split("\\s+")[1],
                    resources.getOrDefault(record.request().split("\\s+")[1], 0L) + 1);
                responseCodes.put(record.request().split("\\s+")[0],
                    resources.getOrDefault(record.request().split("\\s+")[0], 0L) + 1);
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

    private Stream<String> readUrl(String url) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
            return reader.lines();
        }
    }

    private Stream<String> readLocalFiles(String path) throws IOException {

        // todo: *, **
        Path directory = Paths.get(path);

        if (Files.isDirectory(directory)) {
            return Files.walk(directory)
                .filter(Files::isRegularFile)
                .flatMap(file -> {
                    try (BufferedReader reader = Files.newBufferedReader(file)) {
                        return reader.lines();
                    } catch (IOException e) {
                        return Stream.empty();
                    }
                });
        } else {
            try (BufferedReader reader = Files.newBufferedReader(directory)) {
                return reader.lines();
            }
        }
    }

}
