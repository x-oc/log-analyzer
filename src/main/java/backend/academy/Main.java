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
            DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss", Locale.ENGLISH);

        // --path logs/log.txt --from 17/May/2015:08:05:34 --format adoc

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--path" -> path = args[++i];
                case "--from" -> from =  LocalDateTime.parse(args[++i], dateTimeFormatter);
                case "--to" -> to = LocalDateTime.parse(args[++i], dateTimeFormatter);
                case "--format" -> format = args[++i];
                default -> { }
            }
        }

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
                formatter = new MarkdownFormatter();
            } else if (format.equalsIgnoreCase("adoc")) {
                formatter = new AdocFormatter();
            } else {
                System.err.println("Неверный формат вывода.");
            }
        }

        formatter.printMetrics(analysis.metrics());
        formatter.printResources(analysis.resources());
        formatter.printResponseCodes(analysis.responseCodes());

    }
}
