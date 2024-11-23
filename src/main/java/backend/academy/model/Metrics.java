package backend.academy.model;

import lombok.Getter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Metrics {

    private final Map<Metric, String> metrics = new LinkedHashMap<>();

    @Getter
    public enum Metric {
        FILES("Файл(-ы)"),
        START_DATE("Начальная дата"),
        END_DATE("Конечная дата"),
        REQUEST_COUNT("Количество запросов"),
        AVERAGE_RESPONSE_SIZE("Средний размер ответа"),
        RESPONSE_95TH_PERCENTILE("95p размера ответа");

        final String title;

        Metric(String title) {
            this.title = title;
        }

    }

    public void setMetric(Metric metric, String value) {
        metrics.put(metric, value);
    }

    public Map<String, String> getMetrics() {
        return metrics.entrySet().stream()
            .collect(Collectors.toMap(e -> e.getKey().title, Map.Entry::getValue));
    }

    public String getMetric(Metric metric) {
        return metrics.get(metric);
    }
}
