package pl.krasnoludkolo.ebet2.external;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;

import javax.persistence.Id;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
class LeagueDetails {

    @Id
    private UUID leagueUUID;
    private ExternalSourceConfiguration configuration;
    private String clientShortcut;

}
