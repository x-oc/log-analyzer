package backend.academy;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogReader implements Closeable {

    private final Logger logger;
    private final ArrayList<BufferedReader> readers;

    @SuppressFBWarnings("PATH_TRAVERSAL_IN")
    public LogReader(String source) {

        this.readers = new ArrayList<>();
        this.logger = LoggerFactory.getLogger(LogReader.class);

        try {
            if (source.startsWith("http") || source.startsWith("https")) {
                readers.add(new BufferedReader(new InputStreamReader(new URL(source).openStream(),
                                                                        StandardCharsets.UTF_8)));
            } else {
                if (source.contains("*") || Files.isDirectory(Paths.get(source))) {
                    setReadersForLocalFiles(source);
                } else {
                    Path path = Paths.get(source);
                    readers.add(Files.newBufferedReader(path));
                }
            }
        } catch (IOException e) {
            logger.error("Ошибка при чтении файла: {}", source);
        }
    }

    public Stream<String> read() {
        return readers.stream().flatMap(BufferedReader::lines);
    }

    @SuppressFBWarnings("PATH_TRAVERSAL_IN")
    private void setReadersForLocalFiles(String source) throws IOException {

        FileSystem fileSystem = FileSystems.getDefault();
        PathMatcher matcher = fileSystem.getPathMatcher("glob:" + source);

        readers.addAll(Files.walk(Paths.get(source.split("/")[0]))
            .filter(Files::isRegularFile)
            .filter(matcher::matches)
            .map(file -> {
                try {
                    return Files.newBufferedReader(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).toList());
    }

    @Override
    public void close() throws IOException {
        for (BufferedReader reader : readers) {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
