package backend.academy;

import backend.academy.model.Metrics;
import backend.academy.outputFormatters.AdocFormatter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

public class AdocFormatterTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private AdocFormatter adocFormatter;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        adocFormatter = new AdocFormatter();
    }

    @Test
    public void printMetricsTest() {
        Metrics metrics = new Metrics();
        metrics.setMetric(Metrics.Metric.FILES, "test");
        metrics.setMetric(Metrics.Metric.REQUEST_COUNT, "52");
        adocFormatter.printMetrics(metrics);
        String correctOutput = """
            === Общая информация

            |====
            |Метрика |Значение

            |Файл(-ы)
            |test

            |Количество запросов
            |52
            |====""";
        assertThat(outContent.toString().strip().replace("\r", "")).isEqualTo(correctOutput);
    }

    @Test
    public void printResourcesTest() {
        Map<String, String> resources = new HashMap<>();
        resources.put("/test", "3");
        resources.put("/test2", "4");
        adocFormatter.printResources(resources);
        String correctOutput = """
            === Запрашиваемые ресурсы

            |====
            |Ресурс |Количество

            |/test2
            |4

            |/test
            |3
            |====""";
        assertThat(outContent.toString().strip().replace("\r", "")).isEqualTo(correctOutput);
    }

    @Test
    public void printResponseCodesTest() {
        Map<String, String> responseCodes = new HashMap<>();
        responseCodes.put("304", "1");
        responseCodes.put("404", "2");
        responseCodes.put("500", "3");
        adocFormatter.printResponseCodes(responseCodes);
        String correctOutput = """
            === Коды ответа

            |====
            |Код |Имя  |Количество

            |500
            |Internal Server Error
            |3

            |304
            |Not Modified
            |1

            |404
            |Not Found
            |2
            |====""";
        assertThat(outContent.toString().strip().replace("\r", "")).isEqualTo(correctOutput);
    }

    @AfterEach
    public void cleanUpStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }
}
