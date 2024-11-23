package backend.academy.model;

import java.time.LocalDateTime;

public record LogRecord(String remoteAddr, String remoteUser, LocalDateTime timeLocal,
                        LogRequestData request, int status, long bodyBytesSent,
                        String httpReferer, String httpUserAgent) {

    public record LogRequestData(String method, String resource, String protocol) {

    }

}
