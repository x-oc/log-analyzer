package backend.academy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {

    @SuppressWarnings("MagicNumber")
    public LogRecord parseLine(String logLine) {

        Pattern pattern = Pattern.compile(
                "^(\\S+) - (\\S+) \\[(.*?)] \"(.*?)\" (\\d+) (\\d+) \"(.*?)\" \"(.*?)\"$");
        Matcher matcher = pattern.matcher(logLine);

        if (matcher.find()) {
            String remoteAddr = matcher.group(1);
            String remoteUser = matcher.group(2);
            String timeLocalString = matcher.group(3);
            String request = matcher.group(4);
            int status = Integer.parseInt(matcher.group(5));
            long bodyBytesSent = Long.parseLong(matcher.group(6));
            String httpReferer = matcher.group(7);
            String httpUserAgent = matcher.group(8);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
            LocalDateTime timeLocal = LocalDateTime.parse(timeLocalString, formatter);

            return new LogRecord(remoteAddr, remoteUser, timeLocal, request,
                                 status, bodyBytesSent, httpReferer, httpUserAgent);
        } else {
            return null;
        }
    }
}
