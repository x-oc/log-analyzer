package backend.academy;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class LogReader implements Closeable {

    private final BufferedReader reader;
    private final String path;

    public LogReader(String source) throws IOException {
        this.path = source;
        if (source.startsWith("http") || source.startsWith("https")) {
            reader = new BufferedReader(new InputStreamReader(new URL(source).openStream()));
        } else {
            Path path = Paths.get(source);
            if (Files.isDirectory(path)) {
                reader = null;  // Для работы с директорией reader не нужен
            } else {
                reader = Files.newBufferedReader(path);
            }
        }
    }

    public Stream<String> read() throws IOException {
        if (reader != null) {
            return reader.lines();
        } else {
            return readLocalFiles();
        }
    }

    private Stream<String> readLocalFiles() throws IOException {

        // todo: *, **
        Path directory = Paths.get(path);

        return Files.walk(directory)
            .filter(Files::isRegularFile)
            .flatMap(file -> {
                try (BufferedReader reader = Files.newBufferedReader(file)) {
                    return reader.lines();
                } catch (IOException e) {
                    return Stream.empty();
                }
            });
    }

    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }
}
