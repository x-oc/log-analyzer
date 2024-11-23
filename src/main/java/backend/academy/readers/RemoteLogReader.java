package backend.academy.readers;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteLogReader extends LogReader {

    private final static Logger LOGGER = LoggerFactory.getLogger(RemoteLogReader.class);

    @SuppressFBWarnings({"PATH_TRAVERSAL_IN", "URLCONNECTION_SSRF_FD"})
    public RemoteLogReader(String source) {
        try {
            URI uri = new URI(source);
            URL url = uri.toURL();
            URLConnection connection = url.openConnection();
            readers.add(new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)));
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("Ошибка при чтении файла: {}", source);
        }
    }

    public String getFileName(String path) {
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }
}
