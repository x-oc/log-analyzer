package backend.academy;

import backend.academy.outputFormatters.AdocFormatter;
import backend.academy.outputFormatters.MarkdownFormatter;
import backend.academy.outputFormatters.OutputDataFormatter;
import lombok.experimental.UtilityClass;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
//import java.util.HashMap;
//import java.util.Map;

@UtilityClass
public class Main {

    @SuppressWarnings("ModifiedControlVariable")
    public static void main(String[] args) {
        String path = null;
        LocalDateTime from = null;
        LocalDateTime to = null;
        String format = null;


        DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--path" -> path = args[++i];
                case "--from" -> from =  LocalDateTime.parse(args[++i], dateTimeFormatter);
                case "--to" -> to = LocalDateTime.parse(args[++i], dateTimeFormatter);
                case "--format" -> format = args[++i];
                default -> { }
            }
        }

        path = "logs/log.txt";

//        Map<String, String> metrics = new HashMap<>();
//        metrics.put("Файл(-ы)", "access.log");
//        metrics.put("Начальная дата", "31.08.2024");
//        metrics.put("Конечная дата", "-");
//        metrics.put("Количество запросов", "10_000");
//        metrics.put("Средний размер ответа", "500b");
//        metrics.put("95p размера ответа", "950b");
//
//        Map<String, String> resources = new HashMap<>();
//        resources.put("`/index.html`", "5_000");
//        resources.put("`/about.html`", "2_000");
//        resources.put("`/contact.html`", "100");
//
//        Map<String, String> codes = new HashMap<>();
//        codes.put("200", "8000");
//        codes.put("404", "1000");
//        codes.put("500", "500");
//
//        OutputDataFormatter testFormatter = new AdocFormatter();
//        testFormatter.printMetrics(metrics);
//        testFormatter.printResources(resources);
//        testFormatter.printResponseCodes(codes);


        if (path == null) {
            System.err.println("Не указан путь к лог-файлу.");
            return;
        }

        LogAnalyzer logAnalyzer = new LogAnalyzer();
        Analysis analysis;
        try {
            analysis = logAnalyzer.analyzeLogFiles(path, from, to);
        } catch (IOException e) {
            System.err.println("Неверный путь к лог-файлу.");
            return;
        }

        OutputDataFormatter formatter = new MarkdownFormatter();
        if (format != null) {
            if (format.equalsIgnoreCase("markdown")) {
                formatter = new AdocFormatter();
            } else if (format.equalsIgnoreCase("adoc")) {
                formatter = new MarkdownFormatter();
            } else {
                System.err.println("Неверный формат вывода.");
            }
        }

        formatter.printMetrics(analysis.metrics());
        formatter.printResources(analysis.resources());
        formatter.printResponseCodes(analysis.responseCodes());

    }
}
