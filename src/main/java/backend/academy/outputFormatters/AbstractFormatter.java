package backend.academy.outputFormatters;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.math3.util.Pair;

public abstract class AbstractFormatter implements OutputDataFormatter {

    protected final static String COUNT = "Количество";
    protected final PrintStream printStream = new PrintStream(System.out, false, StandardCharsets.UTF_8);

    private static Map<String, String> getCodeDescriptions(String fileName) {
        Map<String, String> codes = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName, StandardCharsets.UTF_8))) {
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

    protected static Map<String, Pair<String, String>> getCodeCountsWithDescriptions(
        String fileName, Map<String, String> responseCodes) {
        Map<String, String> codeDescriptions = getCodeDescriptions(fileName);
        Map<String, Pair<String, String>> codes = new LinkedHashMap<>(responseCodes.size());
        for (Map.Entry<String, String> code : responseCodes.entrySet()) {
            codes.put(code.getKey(), new Pair<>(codeDescriptions.get(code.getKey()), code.getValue()));
        }
        return codes;
    }
}
