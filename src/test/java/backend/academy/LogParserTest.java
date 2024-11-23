package backend.academy;

import backend.academy.model.LogRecord;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import static org.assertj.core.api.Assertions.assertThat;

public class LogParserTest {

    @Test
    public void simpleTest() {
        LogParser logParser = new LogParser();

        LogRecord logRecord = logParser.parseLine("152.90.220.17 - - [17/May/2015:09:05:01 +0000] \"GET " +
            "/downloads/product_2 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.9.7.9)\"");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
        LocalDateTime timeLocal = LocalDateTime.parse("17/May/2015:09:05:01 +0000", formatter);
        var requestData = new LogRecord.LogRequestData("GET", "/downloads/product_2", "HTTP/1.1");

        LogRecord correctLogRecord = new LogRecord("152.90.220.17", "-", timeLocal,
            requestData, 304, 0, "-", "Debian APT-HTTP/1.3 (0.9.7.9)");

        assertThat(logRecord).isEqualTo(correctLogRecord);
    }
}
