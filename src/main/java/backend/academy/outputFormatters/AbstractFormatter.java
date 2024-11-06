package backend.academy.outputFormatters;

import org.apache.commons.math3.util.Pair;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractFormatter implements OutputDataFormatter {

    private static Map<String, String> getResponseCodes(String fileName) {
        Map<String, String> codes = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ", 2);
                codes.put(parts[0], parts[1]);
            }
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }
        return codes;
    }

    protected static Map<String, Pair<String, String>> getCodeCountsWithDescriptions
        (String fileName, Map<String, String> responseCodes) {
        Map<String, String> codeDescriptions = getResponseCodes(fileName);
        Map<String, Pair<String, String>> codes = new HashMap<>();
        for (String code : responseCodes.keySet()) {
            codes.put(code, new Pair<>(codeDescriptions.get(code), responseCodes.get(code)));
        }
        return codes;
    }
}
