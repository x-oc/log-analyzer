package backend.academy.readers;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

public abstract class LogReader implements Closeable {

    protected final ArrayList<BufferedReader> readers;

    public LogReader() {
        this.readers = new ArrayList<>();
    }

    public Stream<String> read() {
        return readers.stream().flatMap(BufferedReader::lines);
    }

    public static boolean isUrl(String path) {
        return path.startsWith("http://") || path.startsWith("https://");
    }

    public abstract String getFileName(String path);

    @Override
    public void close() throws IOException {
        for (BufferedReader reader : readers) {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
