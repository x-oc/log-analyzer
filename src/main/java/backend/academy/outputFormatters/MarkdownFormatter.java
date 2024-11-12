package backend.academy.outputFormatters;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;

public class MarkdownFormatter extends AbstractFormatter {

    protected void printHeader(String header) {
        printStream.println("#### " + header + "\n");
    }

    @SuppressWarnings("MultipleStringLiterals")
    protected void printTableWithTwoColumns(Map<String, String> table, String leftColName, String rightColName) {
        int namesMaxLength = Math.max(table.keySet().stream()
            .mapToInt(String::length)
            .max().orElse(0), leftColName.length());

        int valuesMaxLength = Math.max(table.values().stream()
            .mapToInt(String::length)
            .max().orElse(0), rightColName.length());


        printStream.printf("|%s|%" + (valuesMaxLength + 1) + "s |%n",
            StringUtils.center(leftColName, namesMaxLength + 2), rightColName);
        printStream.printf("|:%s:|%s:|%n", "-".repeat(namesMaxLength), "-".repeat(valuesMaxLength + 1));
        table.forEach((key, value) -> {
            printStream.printf("|%s|%" + (valuesMaxLength + 1) + "s |%n",
                StringUtils.center(key, namesMaxLength + 2), value);
        });
        printStream.println();
    }

    @SuppressWarnings("MultipleStringLiterals")
    protected void printTableWithThreeColumns(Map<String, Pair<String, String>> table,
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

        printStream.printf("|%s|%s|%" + (rightValuesMaxLength + 1) + "s |%n",
            StringUtils.center(leftColName, namesMaxLength + 2),
            StringUtils.center(centerColName, leftValuesMaxLength + 2), rightColName);
        printStream.printf("|:%s:|:%s:|%s:|%n", "-".repeat(namesMaxLength),
            "-".repeat(leftValuesMaxLength), "-".repeat(rightValuesMaxLength + 1));
        table.forEach((key, value) -> {
            printStream.printf("|%s|%s|%" + (rightValuesMaxLength + 1) + "s |%n",
                StringUtils.center(key, namesMaxLength + 2),
                StringUtils.center(value.getFirst(), leftValuesMaxLength + 2),
                value.getSecond());
        });
        printStream.println();
    }
}
