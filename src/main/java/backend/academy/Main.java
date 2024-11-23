package backend.academy;

import backend.academy.model.LogReport;
import backend.academy.outputFormatters.AdocFormatter;
import backend.academy.outputFormatters.MarkdownFormatter;
import backend.academy.outputFormatters.OutputDataFormatter;
import com.beust.jcommander.JCommander;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Main {

    @SuppressFBWarnings("LSC_LITERAL_STRING_COMPARISON")
    public static void main(String[] args) {
        CliParams params = new CliParams();
        JCommander.newBuilder()
            .addObject(params)
            .build()
            .parse(args);

        // --path logs/log2.txt --from 17/May/2015:08:05:34 --format adoc

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss", Locale.ENGLISH);

        LocalDateTime from = null;
        LocalDateTime to = null;

        try {
            from = (params.from() != null) ? LocalDateTime.parse(params.from(), dateTimeFormatter) : null;
            to = (params.to() != null) ? LocalDateTime.parse(params.to(), dateTimeFormatter) : null;
        } catch (Exception e) {
            System.err.println("Неверный формат даты и времени. Используйте dd/MMM/yyyy:HH:mm:ss");
            System.exit(1);
        }

        if (params.path() == null) {
            throw new RuntimeException("Не указан путь к лог-файлу.");
        }

        LogAnalyzer logAnalyzer = new LogAnalyzer(new LogParser());
        LogReport report;
        try {
            report = logAnalyzer.analyzeLogFiles(params.path(), from, to, params.filterField(), params.filterValue());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        OutputDataFormatter formatter = new MarkdownFormatter();
        if (params.format() != null) {
            if (params.format().equalsIgnoreCase("markdown")) {
                formatter = new MarkdownFormatter();
            } else if (params.format().equalsIgnoreCase("adoc")) {
                formatter = new AdocFormatter();
            } else {
                System.err.println("Неверный формат вывода.");
            }
        }

        formatter.printMetrics(report.metrics());
        formatter.printResources(report.resources());
        formatter.printResponseCodes(report.responseCodes());
        formatter.printCustomStatistics(report.remoteUsers(), "Пользователи",
            "Пользователь");
        formatter.printCustomStatistics(report.httpReferrers(), "Рефереры",
            "Реферер");

    }
}
