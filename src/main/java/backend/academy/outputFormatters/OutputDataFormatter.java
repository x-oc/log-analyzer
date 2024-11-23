package backend.academy.outputFormatters;

import backend.academy.model.Metrics;
import java.util.Map;

public interface OutputDataFormatter {

    void printMetrics(Metrics metrics);

    void printResources(Map<String, String> resources);

    void printResponseCodes(Map<String, String> responseCodes);

    void printCustomStatistics(Map<String, String> statistics,
        String tableName, String leftColName, String rightColName);

    void printCustomStatistics(Map<String, String> statistics,
        String tableName, String leftColName);

}
