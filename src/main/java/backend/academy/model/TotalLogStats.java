package backend.academy.model;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Getter
public class TotalLogStats {

    private final Map<String, Long> resources = new HashMap<>();
    private final Map<String, Long> responseCodes = new HashMap<>();
    private final Map<String, Long> remoteUsers = new HashMap<>();
    private final Map<String, Long> httpReferrers = new HashMap<>();
    private final List<Long> responseSizes = new ArrayList<>();
    @Setter
    private AtomicLong totalRequests = new AtomicLong();
    @Setter
    private AtomicLong totalResponseSize = new AtomicLong();

}
