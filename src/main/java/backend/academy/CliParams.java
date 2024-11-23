package backend.academy;

import com.beust.jcommander.Parameter;
import lombok.Getter;

@Getter
public class CliParams {

    @Parameter(names = {"--path"}, description = "Path")
    private String path;

    @Parameter(names = {"--from"}, description = "From date (dd/MMM/yyyy:HH:mm:ss)")
    private String from;

    @Parameter(names = {"--to"}, description = "To date (dd/MMM/yyyy:HH:mm:ss)")
    private String to;

    @Parameter(names = {"--format"}, description = "Format")
    private String format;

    @Parameter(names = {"--filter-field"}, description = "Filter field")
    private String filterField;

    @Parameter(names = {"--filter-value"}, description = "Filter value")
    private String filterValue;

}
