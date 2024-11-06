package backend.academy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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

    public List<String> readLogFiles(String path) throws IOException {
        if (path.startsWith("https://") || path.startsWith("http://")) {
            return readUrl(path);
        } else {
            return readLocalFiles(path);
        }
    }

    private List<String> readUrl(String url) throws IOException {
        final List<String> logRecords = new ArrayList<>();
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                logRecords.add(inputLine);
            }
            in.close();
        }

        connection.disconnect();
        return logRecords;
    }

    private List<String> readLocalFiles(String path) throws IOException {
        // todo: *, **
        final List<String> logRecords = new ArrayList<>();
        Path directory = Paths.get(path);
        if (Files.isDirectory(directory)) {
            Files.walk(directory)
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    try (BufferedReader reader = Files.newBufferedReader(file)) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            logRecords.add(line);
                        }
                    } catch (IOException e) {
                        System.err.println("Ошибка при чтении файла: " + file + " - " + e.getMessage());
                    }
                });
        } else {
            try (BufferedReader reader = Files.newBufferedReader(directory)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logRecords.add(line);
                }
            }
        }
        return logRecords;
    }

}
