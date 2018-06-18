package pl.krasnoludkolo.ebet2.league;

import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.infrastructure.TimeProvider;
import pl.krasnoludkolo.ebet2.league.api.*;

import javax.transaction.Transactional;
import java.util.UUID;

@Transactional
public class LeagueFacade {

    private LeagueManager leagueManager;
    private MatchManager matchManager;
    private TimeProvider timeProvider;

    public LeagueFacade(LeagueManager leagueManager, MatchManager matchManager, TimeProvider timeProvider) {
        this.leagueManager = leagueManager;
        this.matchManager = matchManager;
        this.timeProvider = timeProvider;
    }

    public UUID createLeague(String name) {
        League league = leagueManager.createLeague(name);
        return league.getUuid();
    }

    public Option<LeagueDTO> findLeagueByName(String name) {
        return leagueManager.findLeagueByName(name).map(League::toDTO);
    }

    public List<LeagueDetailsDTO> getAllLeaguesDetails() {
        return leagueManager.getAllLeagues().map(l -> new LeagueDetailsDTO(l.getUuid(), l.getName()));
    }

    public Either<String, UUID> addMatchToLeague(NewMatchDTO newMatchDTO) {
        UUID leagueUUID = newMatchDTO.getLeagueUUID();
        return leagueManager.addMatchToLeague(leagueUUID, newMatchDTO);
    }

    public List<MatchDTO> getMatchesFromRound(UUID leagueUUID, int round) {
        return leagueManager.getMatchesFromRound(leagueUUID, round);
    }

    public Option<MatchDTO> getMatchByUUID(UUID uuid) {
        return matchManager.findByUUID(uuid).map(Match::toDTO);
    }

    public Option<LeagueDTO> getLeagueByUUID(UUID uuid) {
        return leagueManager.findLeagueByUUID(uuid).map(League::toDTO);
    }

    public void setMatchResult(UUID matchUUID, MatchResult result) {
        matchManager.setMatchResult(matchUUID, result);
    }

    public void removeMatch(UUID matchUUID) {
        matchManager.removeMatchByUUID(matchUUID);
    }

    public void removeLeague(UUID leagueUUID) {
        leagueManager.removeMatchUUID(leagueUUID);
    }

    public List<MatchDTO> getAllMatchesFromLeague(UUID uuid) {
        return leagueManager.getAllMatchesFromLeague(uuid);
    }

    public Either<String, Boolean> hasMatchAlreadyBegun(UUID matchUUID) {
        return matchManager
                .findByUUID(matchUUID)
                .toEither("Match not found")
                .map(match -> match.hasAlreadyBegun(timeProvider.now()));
    }
}
