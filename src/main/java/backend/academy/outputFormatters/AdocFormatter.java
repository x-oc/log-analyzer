package backend.academy.outputFormatters;

import org.apache.commons.math3.util.Pair;
import java.util.HashMap;
import java.util.Map;

public class AdocFormatter extends AbstractFormatter {

    @Override
    public void printMetrics(Map<String, String> metrics) {
        System.out.println("=== Общая информация\n");
        printTableWithTwoColumns(metrics, "Метрика", "Значение");
        System.out.println();
    }

    @Override
    public void printResources(Map<String, String> resources) {
        System.out.println("=== Запрашиваемые ресурсы\n");
        printTableWithTwoColumns(resources, "Ресурс", "Количество");
        System.out.println();
    }

    @Override
    public void printResponseCodes(Map<String, String> responseCodes) {
        System.out.println("=== Коды ответа\n");
        Map<String, Pair<String, String>> codes = getCodeCountsWithDescriptions("responseCodes.txt", responseCodes);
        printTableWithThreeColumns(codes, "Код", "Имя", "Количество");
        System.out.println();
    }

    private void printTableWithTwoColumns(Map<String, String> table, String leftColName, String rightColName) {
        System.out.println("|====");
        System.out.printf("|%s |%s\n", leftColName, rightColName);
        for (Map.Entry<String, String> entry : table.entrySet()) {
            System.out.printf("\n|%s\n|%s\n", entry.getKey(), entry.getValue());
        }
        System.out.println("|====\n");
    }

    private void printTableWithThreeColumns(Map<String, Pair<String, String>> table,
        String leftColName, String centerColName, String rightColName) {
        System.out.println("|====");
        System.out.printf("|%s |%s  |%s\n", leftColName, centerColName, rightColName);
        for (Map.Entry<String, Pair<String, String>> entry : table.entrySet()) {
            System.out.printf("\n|%s\n|%s\n|%s\n", entry.getKey(), entry.getValue().getFirst(), entry.getValue().getSecond());
        }
        System.out.println("|====\n");
    }
}
