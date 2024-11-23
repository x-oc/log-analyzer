package backend.academy.logFieldFilters;

public class FieldFilterFactory {

    public static LogFieldFilter resolveFilter(String name) {
        return switch (name) {
            case "addr" -> new AddressFilter();
            case "user" -> new UserFilter();
            case "time" -> new TimeFilter();
            case "request" -> new RequestFilter();
            case "status" -> new StatusFilter();
            case "bytes" -> new BytesFilter();
            case "referrer" -> new ReferrerFilter();
            case "agent" -> new AgentFilter();
            default -> (_, _) -> true;
        };
    }

}
