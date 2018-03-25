package pl.krasnoludkolo.ebet2.autoimport;

import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.autoimport.api.MissingConfigurationException;

import java.util.HashMap;
import java.util.Map;

class ExternalSourceConfiguration {

    private Map<String, String> config = new HashMap<>();

    void putParameter(String parameter, String value) {
        config.put(parameter, value);
    }

    String getParameter(String parameter) {
        return Option.of(config.get(parameter)).getOrElseThrow(() -> {
            throw new MissingConfigurationException("Missing parameter: " + parameter);
        });
    }

}
