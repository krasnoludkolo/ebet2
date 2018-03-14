package pl.krasnoludkolo.ebet2.league.domain;

import io.vavr.collection.List;
import lombok.Getter;
import pl.krasnoludkolo.ebet2.league.api.LeagueDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchDTO;

import java.util.UUID;

@Getter
class League {

    private UUID uuid;
    private final String name;
    private List<Match> matches;

    League(String name) {
        this.name = name;
        matches = List.empty();
        uuid = UUID.randomUUID();
    }

    void addMatch(Match match) {
        matches = matches.append(match);
    }

    LeagueDTO toDTO() {
        List<MatchDTO> matchDTOS = matches.map(Match::toDTO);
        return new LeagueDTO(uuid, name, matchDTOS);
    }

}
