package backend.academy;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogReader implements Closeable {

    private final static Logger LOGGER = LoggerFactory.getLogger(LogReader.class);
    private final static String GLOB_STR = "glob:";
    private final ArrayList<BufferedReader> readers;

    @SuppressFBWarnings({"PATH_TRAVERSAL_IN", "URLCONNECTION_SSRF_FD"})
    public LogReader(String source) {

        this.readers = new ArrayList<>();

        try {
            if (isUrl(source)) {
                URI uri = new URI(source);
                URL url = uri.toURL();
                URLConnection connection = url.openConnection();
                readers.add(new BufferedReader(new InputStreamReader(connection.getInputStream(),
                                                                        StandardCharsets.UTF_8)));
            } else {
                if (source.contains("*") || Files.isDirectory(Paths.get(source))) {
                    setReadersForLocalFiles(source);
                } else {
                    readers.add(Files.newBufferedReader(Paths.get(source)));
                }
            }
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("Ошибка при чтении файла: {}", source);
        }
    }

    public Stream<String> read() {
        return readers.stream().flatMap(BufferedReader::lines);
    }

    @SuppressFBWarnings("PATH_TRAVERSAL_IN")
    private void setReadersForLocalFiles(String source) throws IOException {

        FileSystem fileSystem = FileSystems.getDefault();
        PathMatcher matcher = fileSystem.getPathMatcher(GLOB_STR + source);

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

    @SuppressFBWarnings("PATH_TRAVERSAL_IN")
    public String getFileName(String path) {
        File file = new File(path);
        StringBuilder name = new StringBuilder();
        String fileNameNotFound = "Не удалось определить название файла";

        if (isUrl(path)) {
            String[] parts = path.split("/");
            name.append(parts[parts.length - 1]);
        } else if (path.contains("*") || Files.isDirectory(Paths.get(path))) {
            FileSystem fileSystem = FileSystems.getDefault();
            PathMatcher matcher = fileSystem.getPathMatcher(GLOB_STR + path);
            List<String> files;
            try {
                files = Files.walk(Paths.get(path.split("/")[0]))
                    .filter(Files::isRegularFile)
                    .filter(matcher::matches)
                    .map(source -> {
                        String[] parts = source.toString().split(Pattern.quote("\\"));
                        return parts[parts.length - 1];
                    })
                    .toList();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            for (String f : files) {
                if (f != null && !f.isEmpty()) {
                    name.append(f).append(' ');
                }
            }
        } else if (file.isFile()) {
            name.append(file.getName());
        } else {
            name.append(fileNameNotFound);
        }
        return name.toString();
    }

    private boolean isUrl(String path) {
        return path.startsWith("http://") || path.startsWith("https://");
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
