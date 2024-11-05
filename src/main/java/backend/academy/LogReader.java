package backend.academy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LogReader {

    private final LogParser parser;

    public LogReader() {
        parser = new LogParser();
    }

    private List<LogRecord> readLogFiles(String path) throws IOException {
        if (path.startsWith("https://") || path.startsWith("http://")) {
            return readUrl(path);
        } else {
            return readLocalFiles(path);
        }
    }

    private List<LogRecord> readUrl(String url) throws IOException {
        final List<LogRecord> logRecords = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                LogRecord record = parser.parseLine(line);
                if (record != null) {
                    logRecords.add(record);
                }
            }
        }
        return logRecords;
    }

    private List<LogRecord> readLocalFiles(String path) throws IOException {
        final List<LogRecord> logRecords = new ArrayList<>();
        Path directory = Paths.get(path);
        if (Files.isDirectory(directory)) {
            Files.walk(directory)
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    try (BufferedReader reader = Files.newBufferedReader(file)) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            LogRecord record = parser.parseLine(line);
                            if (record != null) {
                                logRecords.add(record);
                            }
                        }
                    } catch (IOException e) {
                        System.err.println("Ошибка при чтении файла: " + file.toString() + " - " + e.getMessage());
                    }
                });
        } else {
            try (BufferedReader reader = Files.newBufferedReader(directory)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    LogRecord record = parser.parseLine(line);
                    if (record != null) {
                        logRecords.add(record);
                    }
                }
            }
        }
        return logRecords;
    }

}
