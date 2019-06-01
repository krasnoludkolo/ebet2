package pl.krasnoludkolo.ebet2.external.api;

import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.TreeMap;


public class ExternalSourceConfiguration {

    private final Map<String, String> config;
    private final String clientShortcut;

    public static ExternalSourceConfiguration fromSettingsList(List<Tuple2<String, String>> settings, String clientShortcut) {
        Map<String, String> config = settings.toMap(x -> x);
        return new ExternalSourceConfiguration(config, clientShortcut);
    }

    public static ExternalSourceConfiguration empty(String clientShortcut) {
        return new ExternalSourceConfiguration(TreeMap.empty(), clientShortcut);
    }

    public ExternalSourceConfiguration(Map<String, String> config, String clientShortcut) {
        this.config = config;
        this.clientShortcut = clientShortcut;
    }

    public String getParameter(String parameter) {
        return config.get(parameter).getOrElseThrow(() -> {
            throw new MissingConfigurationException("Missing parameter: " + parameter);
        });
    }

    public List<Tuple2<String, String>> getAllSettings() {
        return config.toList();
    }

    public String getClientShortcut() {
        return clientShortcut;
    }
}
