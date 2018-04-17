package pl.krasnoludkolo.ebet2.external.api;

import io.vavr.control.Option;

import java.util.HashMap;
import java.util.Map;

public class ExternalSourceConfiguration {

    private Map<String, String> config = new HashMap<>();

    public void putParameter(String parameter, String value) {
        config.put(parameter, value);
    }

    public String getParameter(String parameter) {
        return Option.of(config.get(parameter)).getOrElseThrow(() -> {
            throw new MissingConfigurationException("Missing parameter: " + parameter);
        });
    }

}
