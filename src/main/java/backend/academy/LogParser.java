package backend.academy;

import backend.academy.model.LogRecord;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {

    private static final Pattern PATTERN = Pattern.compile(
        "^(\\S+) - (\\S+) \\[(.*?)] \"(.*?)\" (\\d+) (\\d+) \"(.*?)\" \"(.*?)\"$");

    @SuppressWarnings("MagicNumber")
    public LogRecord parseLine(String logLine) {

        Matcher matcher = PATTERN.matcher(logLine);

        if (!matcher.find()) {
            return null;
        }

        LogRecord.LogRecordBuilder logRecordBuilder = LogRecord.builder();

        logRecordBuilder.remoteAddr(matcher.group(1));
        logRecordBuilder.remoteUser(matcher.group(2));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
        logRecordBuilder.timeLocal(LocalDateTime.parse(matcher.group(3), formatter));

        String[] request = matcher.group(4).split(" ");
        if (request.length != 3) {
            return null;
        }
        LogRecord.LogRequestData requestData = new LogRecord.LogRequestData(request[0], request[1], request[2]);
        logRecordBuilder.request(requestData);

        logRecordBuilder.status(Integer.parseInt(matcher.group(5)));
        logRecordBuilder.bodyBytesSent(Long.parseLong(matcher.group(6)));
        logRecordBuilder.httpReferer(matcher.group(7));
        logRecordBuilder.httpUserAgent(matcher.group(8));

        return logRecordBuilder.build();
    }
}
