package backend.academy.outputFormatters;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import java.util.HashMap;
import java.util.Map;

public class MarkdownFormatter extends AbstractFormatter {

    @Override
    public void printMetrics(Map<String, String> metrics) {
        System.out.println("#### Общая информация\n");
        printTableWithTwoColumns(metrics, "Метрика", "Значение");
        System.out.println();
    }

    @Override
    public void printResources(Map<String, String> resources) {
        System.out.println("#### Запрашиваемые ресурсы\n");
        printTableWithTwoColumns(resources, "Ресурс", "Количество");
        System.out.println();
    }

    @Override
    public void printResponseCodes(Map<String, String> responseCodes) {
        System.out.println("#### Коды ответа\n");
        Map<String, Pair<String, String>> codes = getCodeCountsWithDescriptions("responseCodes.txt", responseCodes);
        printTableWithThreeColumns(codes, "Код", "Имя", "Количество");
        System.out.println();
    }

    private void printTableWithTwoColumns(Map<String, String> table, String leftColName, String rightColName) {
        int namesMaxLength = Math.max(table.keySet().stream()
            .mapToInt(String::length)
            .max().orElse(0), leftColName.length());

        int valuesMaxLength = Math.max(table.values().stream()
            .mapToInt(String::length)
            .max().orElse(0), rightColName.length());


        System.out.printf("|%s|%" + (valuesMaxLength + 1) + "s |%n",
            StringUtils.center(leftColName, namesMaxLength + 2), rightColName);
        System.out.printf("|:%s:|%s:|%n", "-".repeat(namesMaxLength), "-".repeat(valuesMaxLength + 1));
        table.forEach((key, value) -> {
            System.out.printf("|%s|%" + (valuesMaxLength + 1) + "s |%n",
                StringUtils.center(key, namesMaxLength + 2), value);
        });
    }

    private void printTableWithThreeColumns(Map<String, Pair<String, String>> table,
                                            String leftColName, String centerColName, String rightColName) {

        int namesMaxLength = Math.max(table.keySet().stream()
            .mapToInt(String::length)
            .max().orElse(0), leftColName.length());

        int leftValuesMaxLength = Math.max(table.values().stream()
            .mapToInt(value -> value.getFirst().length())
            .max().orElse(0), centerColName.length());

        int rightValuesMaxLength = Math.max(table.values().stream()
            .mapToInt(value -> value.getSecond().length())
            .max().orElse(0), rightColName.length());

        System.out.printf("|%s|%s|%" + (rightValuesMaxLength + 1) + "s |%n",
            StringUtils.center(leftColName, namesMaxLength + 2),
            StringUtils.center(centerColName, leftValuesMaxLength + 2), rightColName);
        System.out.printf("|:%s:|:%s:|%s:|%n", "-".repeat(namesMaxLength),
            "-".repeat(leftValuesMaxLength), "-".repeat(rightValuesMaxLength + 1));
        table.forEach((key, value) -> {
            System.out.printf("|%s|%s|%" + (rightValuesMaxLength + 1) + "s |%n",
                StringUtils.center(key, namesMaxLength + 2),
                StringUtils.center(value.getFirst(), leftValuesMaxLength + 2),
                value.getSecond());
        });
    }
}
