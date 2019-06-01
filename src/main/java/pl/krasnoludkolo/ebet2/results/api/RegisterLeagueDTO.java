package pl.krasnoludkolo.ebet2.results.api;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class RegisterLeagueDTO {

    private String leagueName;
    private String clientShortcut;
    private Map<String, String> config;

}
