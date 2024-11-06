package backend.academy;

import backend.academy.outputFormatters.MarkdownFormatter;
import backend.academy.outputFormatters.AdocFormatter;
import backend.academy.outputFormatters.OutputDataFormatter;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Main {
    public static void main(String[] args) {
        String path = null;
        String from = null;
        String to = null;
        String format = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--path" -> path = args[++i];
                case "--from" -> from = args[++i];
                case "--to" -> to = args[++i];
                case "--format" -> format = args[++i];
            }
        }

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



    }
}
