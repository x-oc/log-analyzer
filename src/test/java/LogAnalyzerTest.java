import backend.academy.LogReport;
import backend.academy.LogAnalyzer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import static org.assertj.core.api.Assertions.assertThat;

public class LogAnalyzerTest {

    private static LogAnalyzer logAnalyzer;

    @BeforeAll
    public static void setup() {
        logAnalyzer = new LogAnalyzer();
    }

    @Test
    public void localAnalysisTest() {

        try {
            LogReport logReport = logAnalyzer.analyzeLogFiles("logs/log/log.txt", null, null);

            HashMap<String, String> metrics = new LinkedHashMap<>();
            metrics.put("Файл(-ы)", "log.txt");
            metrics.put("Начальная дата", "-");
            metrics.put("Конечная дата", "-");
            metrics.put("Количество запросов", "19");
            metrics.put("Средний размер ответа", "311b");
            metrics.put("95p размера ответа", "772b");
            HashMap<String, String> resources = new LinkedHashMap<>();
            resources.put("/downloads/product_1", "12");
            resources.put("/downloads/product_2", "7");
            HashMap<String, String> responseCodes = new LinkedHashMap<>();
            responseCodes.put("200", "3");
            responseCodes.put("304", "11");
            responseCodes.put("404", "5");
            LogReport correctLogReport = new LogReport(metrics, resources, responseCodes);

            assertThat(logReport).isEqualTo(correctLogReport);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void fromToTest() {
        try {
            DateTimeFormatter dateTimeFormatter =
                DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss", Locale.ENGLISH);
            LocalDateTime from = LocalDateTime.parse("17/May/2015:08:05:23", dateTimeFormatter);
            LocalDateTime to = LocalDateTime.parse("17/May/2015:08:05:42", dateTimeFormatter);

            LogReport logReport = logAnalyzer.analyzeLogFiles("logs/log/log.txt", from, to);

            String correctFrom = "2015-05-17T08:05:23";
            String correctTo = "2015-05-17T08:05:42";
            String correctCount = "9";

            assertThat(logReport.metrics().get("Начальная дата")).isEqualTo(correctFrom);
            assertThat(logReport.metrics().get("Конечная дата")).isEqualTo(correctTo);
            assertThat(logReport.metrics().get("Количество запросов")).isEqualTo(correctCount);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void urlAnalysisTest() {
        try {
            LogReport logReport = logAnalyzer.analyzeLogFiles("https://raw.githubusercontent.com/elastic/examples/master/Common%20Data%20Formats/nginx_logs/nginx_logs", null, null);

            String correctFiles = "nginx_logs";
            String correctCount = "51462";

            assertThat(logReport.metrics().get("Файл(-ы)")).isEqualTo(correctFiles);
            assertThat(logReport.metrics().get("Количество запросов")).isEqualTo(correctCount);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void globTest() {
        try {
            LogReport logReport = logAnalyzer.analyzeLogFiles("logs/**", null, null);

            String correctFiles = "log.txt log2.txt ";
            String correctCount = "26";

            assertThat(logReport.metrics().get("Файл(-ы)")).isEqualTo(correctFiles);
            assertThat(logReport.metrics().get("Количество запросов")).isEqualTo(correctCount);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
