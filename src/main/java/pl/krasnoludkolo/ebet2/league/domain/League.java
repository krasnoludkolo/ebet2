package pl.krasnoludkolo.ebet2.league.domain;

import io.vavr.collection.List;
import pl.krasnoludkolo.ebet2.league.api.LeagueDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchDTO;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

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

    public List<MatchDTO> getMatchesForRound(int round) {
        return matches
                .filter(correspondentRound(round))
                .map(Match::toDTO);
    }

    private Predicate<Match> correspondentRound(int round) {
        return match -> match.getRound() == round;
    }

    boolean hasName(String name) {
        return Objects.equals(this.name, name);
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        League league = (League) o;
        return Objects.equals(uuid, league.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name, matches);
    }

}
