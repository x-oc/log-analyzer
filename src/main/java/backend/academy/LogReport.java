package backend.academy;

import java.util.Map;

public record LogReport(Map<String, String> metrics,
                        Map<String, String> resources,
                        Map<String, String> responseCodes,
                        Map<String, String> remoteUsers,
                        Map<String, String> httpReferrers) {
}
