package backend.academy;

import com.google.common.math.Quantiles;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.util.Map.Entry;

public class LogAnalyzer {

    private final LogParser parser;
    private final static int P95 = 95;

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

            long response95p = (long) Quantiles.percentiles().index(P95).compute(responseSizes);
            Map<String, String> metrics = new LinkedHashMap<>();
            metrics.put("Файл(-ы)", getFileName(path));
            metrics.put("Начальная дата", from == null ? "-" : from.toString());
            metrics.put("Конечная дата", to == null ? "-" : to.toString());
            metrics.put("Количество запросов", totalRequests.toString());
            metrics.put("Средний размер ответа", totalResponseSize.get() / totalRequests.get() + "b");
            metrics.put("95p размера ответа", response95p + "b");

            return new Analysis(metrics, mapLongToString(resources), mapLongToString(responseCodes));
        }
    }

    @SuppressFBWarnings("PATH_TRAVERSAL_IN")
    private String getFileName(String path) {
        File file = new File(path);
        StringBuilder name = new StringBuilder();
        String fileNameNotFound = "Не удалось определить название файла";

        if (path.startsWith("http") || path.startsWith("https")) {
            try {
                URL url = new URL(path);
                name.append(url.getPath().substring(url.getPath().lastIndexOf('/') + 1));
            } catch (MalformedURLException e) {
                name.append(fileNameNotFound);
            }
        } else if (path.contains("*") || Files.isDirectory(Paths.get(path))) {
            FileSystem fileSystem = FileSystems.getDefault();
            PathMatcher matcher = fileSystem.getPathMatcher("glob:" + path);
            List<String> files;
            try {
                files = Files.walk(Paths.get(path.split("/")[0]))
                    .filter(Files::isRegularFile)
                    .filter(matcher::matches)
                    .map(source -> {
                        String[] parts = source.toString().split(Pattern.quote("\\"));
                        return parts[parts.length - 1];
                    })
                    .toList();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            for (String f : files) {
                if (f != null && !f.isEmpty()) {
                    name.append(f).append(' ');
                }
            }
        } else if (file.isFile()) {
            name.append(file.getName());
        } else {
            name.append(fileNameNotFound);
        }
        return name.toString();
    }

    private Map<String, String> mapLongToString(Map<String, Long> map) {
        return map.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> String.valueOf(e.getValue())));
    }
}
