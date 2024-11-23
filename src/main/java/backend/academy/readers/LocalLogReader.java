package backend.academy.readers;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalLogReader extends LogReader {

    private final static Logger LOGGER = LoggerFactory.getLogger(LocalLogReader.class);
    private final static String GLOB_STR = "glob:";

    @SuppressFBWarnings("PATH_TRAVERSAL_IN")
    public LocalLogReader(String source) {
        try {
            if (source.contains("*") || Files.isDirectory(Paths.get(source))) {
                setReadersForLocalFiles(source);
            } else {
                readers.add(Files.newBufferedReader(Paths.get(source)));
            }
        } catch (IOException e) {
            LOGGER.error("Ошибка при чтении файла: {}", source);
        }
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

        if (path.contains("*") || Files.isDirectory(Paths.get(path))) {
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

}
