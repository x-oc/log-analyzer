package backend.academy;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Main {
    public static void main(String[] args) {
        String path = null;
        String from = null;
        String to = null;
        String format = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--path" -> path = args[++i];
                case "--from" -> from = args[++i];
                case "--to" -> to = args[++i];
                case "--format" -> format = args[++i];
            }
        }

        if (path == null) {
            System.err.println("Не указан путь к лог-файлу.");
            return;
        }

        OutputDataFormatter formatter = new AdocFormatter();
        if (format != null) {
            if (format.equalsIgnoreCase("markdown")) {
                formatter = new MarkdownFormatter();
            } else if (format.equalsIgnoreCase("adoc")) {
                formatter = new AdocFormatter();
            } else {
                System.err.println("Неверный формат вывода.");
            }
        }



    }
}
