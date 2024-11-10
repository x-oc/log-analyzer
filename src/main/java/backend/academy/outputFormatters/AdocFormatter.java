package backend.academy.outputFormatters;

import java.util.Map;
import org.apache.commons.math3.util.Pair;

public class AdocFormatter extends AbstractFormatter {

    @Override
    public void printMetrics(Map<String, String> metrics) {
        printStream.println("=== Общая информация\n");
        printTableWithTwoColumns(metrics, "Метрика", "Значение");
        printStream.println();
    }

    @Override
    public void printResources(Map<String, String> resources) {
        printStream.println("=== Запрашиваемые ресурсы\n");
        printTableWithTwoColumns(resources, "Ресурс", COUNT);
        printStream.println();
    }

    @Override
    public void printResponseCodes(Map<String, String> responseCodes) {
        printStream.println("=== Коды ответа\n");
        Map<String, Pair<String, String>> codes = getCodeCountsWithDescriptions("responseCodes.txt", responseCodes);
        printTableWithThreeColumns(codes, "Код", "Имя", COUNT);
        printStream.println();
    }

    private void printTableWithTwoColumns(Map<String, String> table, String leftColName, String rightColName) {
        printTableHeader();
        printStream.printf("|%s |%s%n", leftColName, rightColName);
        for (Map.Entry<String, String> entry : table.entrySet()) {
            printStream.printf("%n|%s%n|%s%n", entry.getKey(), entry.getValue());
        }
        printTableFooter();
    }

    private void printTableWithThreeColumns(Map<String, Pair<String, String>> table,
        String leftColName, String centerColName, String rightColName) {
        printTableHeader();
        printStream.printf("|%s |%s  |%s%n", leftColName, centerColName, rightColName);
        for (Map.Entry<String, Pair<String, String>> entry : table.entrySet()) {
            printStream.printf("%n|%s%n|%s%n|%s%n",
                entry.getKey(), entry.getValue().getFirst(), entry.getValue().getSecond());
        }
        printTableFooter();
    }

    private void printTableHeader() {
        printStream.println("|====");
    }

    private void printTableFooter() {
        printStream.println("|====\n");
    }
}
