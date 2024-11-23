package backend.academy.model;

import java.util.Map;
import lombok.Builder;

@Builder
public record LogReport(Metrics metrics,
                        Map<String, String> resources,
                        Map<String, String> responseCodes,
                        Map<String, String> remoteUsers,
                        Map<String, String> httpReferrers) {
}
