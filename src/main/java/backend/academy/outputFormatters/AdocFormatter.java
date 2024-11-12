package backend.academy.outputFormatters;

import java.util.Map;
import org.apache.commons.math3.util.Pair;

public class AdocFormatter extends AbstractFormatter {

    protected void printHeader(String header) {
        printStream.println("=== " + header + "\n");
    }

    protected void printTableWithTwoColumns(Map<String, String> table, String leftColName, String rightColName) {
        printTableHeader();
        printStream.printf("|%s |%s%n", leftColName, rightColName);
        for (Map.Entry<String, String> entry : table.entrySet()) {
            printStream.printf("%n|%s%n|%s%n", entry.getKey(), entry.getValue());
        }
        printTableFooter();
        printStream.println();
    }

    protected void printTableWithThreeColumns(Map<String, Pair<String, String>> table,
        String leftColName, String centerColName, String rightColName) {
        printTableHeader();
        printStream.printf("|%s |%s  |%s%n", leftColName, centerColName, rightColName);
        for (Map.Entry<String, Pair<String, String>> entry : table.entrySet()) {
            printStream.printf("%n|%s%n|%s%n|%s%n",
                entry.getKey(), entry.getValue().getFirst(), entry.getValue().getSecond());
        }
        printTableFooter();
        printStream.println();
    }

    private void printTableHeader() {
        printStream.println("|====");
    }

    private void printTableFooter() {
        printStream.println("|====\n");
    }
}
