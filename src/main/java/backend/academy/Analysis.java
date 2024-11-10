package backend.academy;

import java.util.Map;

public record Analysis(Map<String, String> metrics,
                       Map<String, String> resources,
                       Map<String, String> responseCodes) {
}
