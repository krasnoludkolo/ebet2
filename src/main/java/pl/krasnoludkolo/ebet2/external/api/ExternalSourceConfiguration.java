package pl.krasnoludkolo.ebet2.external.api;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Option;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ExternalSourceConfiguration {

    private Map<String, String> config = new HashMap<>();

    public ExternalSourceConfiguration() {
    }

    public ExternalSourceConfiguration(Map<String, String> config) {
        this.config = config;
    }

    public void putParameter(String parameter, String value) {
        config.put(parameter, value);
    }

    public String getParameter(String parameter) {
        return Option.of(config.get(parameter)).getOrElseThrow(() -> {
            throw new MissingConfigurationException("Missing parameter: " + parameter);
        });
    }

    public List<Tuple2<String, String>> getAllSettings() {
        return List.ofAll(config.entrySet().stream().map(entry -> Tuple.of(entry.getKey(), entry.getValue())).collect(Collectors.toList()));
    }
}
