package pl.krasnoludkolo.ebet2.league;

import io.vavr.collection.List;
import lombok.NoArgsConstructor;
import pl.krasnoludkolo.ebet2.league.api.dto.LeagueDTO;
import pl.krasnoludkolo.ebet2.league.api.dto.MatchDTO;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

@Entity
@NoArgsConstructor
class League {

    @Id
    private UUID uuid;
    private String name;
    @OneToMany(mappedBy = "league")
    private java.util.List<Match> matches = new ArrayList<>();
    private boolean archived;

    static League createWithName(String name) {
        return new League(name);
    }

    private League(String name) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.archived = false;
    }

    void addMatch(Match match) {
        matches.add(match);
    }

    List<MatchDTO> getMatchesForRound(int round) {
        return List.ofAll(matches)
                .filter(correspondentRound(round))
                .map(Match::toDTO);
    }

    List<MatchDTO> getAllMatches() {
        return List.ofAll(matches).map(Match::toDTO);
    }

    private Predicate<Match> correspondentRound(int round) {
        return match -> match.hasRoundNumber(round);
    }

    boolean hasName(String name) {
        return Objects.equals(this.name, name);
    }

    void archive() {
        this.archived = true;
    }

    UUID getUuid() {
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

    LeagueDTO toDTO() {
        List<MatchDTO> matchDTOS = List.ofAll(matches).map(Match::toDTO);
        return new LeagueDTO(uuid, name, matchDTOS, archived);
    }
}
