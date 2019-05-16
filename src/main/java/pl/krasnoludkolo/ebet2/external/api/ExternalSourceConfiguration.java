package pl.krasnoludkolo.ebet2.external.api;

import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.TreeMap;


public class ExternalSourceConfiguration {

    private final Map<String, String> config;

    public static ExternalSourceConfiguration fromSettingsList(List<Tuple2<String, String>> settings) {
        Map<String, String> config = settings.toMap(x -> x);
        return new ExternalSourceConfiguration(config);
    }

    public static ExternalSourceConfiguration empty() {
        return new ExternalSourceConfiguration(TreeMap.empty());
    }

    public ExternalSourceConfiguration(Map<String, String> config) {
        this.config = config;
    }

    public String getParameter(String parameter) {
        return config.get(parameter).getOrElseThrow(() -> {
            throw new MissingConfigurationException("Missing parameter: " + parameter);
        });
    }

    public List<Tuple2<String, String>> getAllSettings() {
        return config.toList();
    }
}
