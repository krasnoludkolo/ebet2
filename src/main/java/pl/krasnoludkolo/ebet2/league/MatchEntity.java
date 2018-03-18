package pl.krasnoludkolo.ebet2.league;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
class MatchEntity {

    @Id
    private UUID uuid;
    private int round;
    private String host;
    private String guest;
    private MatchResult result;
    @ManyToOne
    private LeagueEntity league;


}
