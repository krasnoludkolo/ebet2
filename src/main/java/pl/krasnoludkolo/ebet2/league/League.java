package pl.krasnoludkolo.ebet2.league;

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

    static League createWithName(String name) {
        return new League(name);
    }

    static League fromEntity(LeagueEntity leagueEntity) {
        UUID uuid = leagueEntity.getUuid();
        String name = leagueEntity.getName();
        League league = new League(uuid, name, List.empty());
        List<Match> matches = List.ofAll(leagueEntity.getMatches()).map((MatchEntity entity) -> Match.fromEntity(entity, league));
        matches.forEach(league::addMatch);
        return league;
    }

    private League(String name) {
        this(UUID.randomUUID(), name, List.empty());
    }

    private League(UUID uuid, String name, List<Match> matches) {
        this.uuid = uuid;
        this.name = name;
        this.matches = matches;
    }

    void addMatch(Match match) {
        matches = matches.append(match);
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

    public LeagueEntity toEntity() {
        LeagueEntity leagueEntity = new LeagueEntity(uuid, name, null);
        java.util.List<MatchEntity> matchesEntity = matches.map(match -> match.toEntity(leagueEntity)).asJava();
        leagueEntity.setMatches(matchesEntity);
        return leagueEntity;

    }

    LeagueDTO toDTO() {
        List<MatchDTO> matchDTOS = matches.map(Match::toDTO);
        return new LeagueDTO(uuid, name, matchDTOS);
    }
}
