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

    public Either<LeagueError, UUID> createLeague(String name) {
        return leagueManager
                .createLeague(name)
                .map(League::getUuid);
    }

    public Option<LeagueDTO> findLeagueByName(String name) {
        return leagueManager.findLeagueByName(name).map(League::toDTO);
    }

    public List<LeagueDetailsDTO> getAllLeaguesDetails() {
        return leagueManager.getAllLeagues().map(l -> new LeagueDetailsDTO(l.getUuid(), l.getName(), l.isArchived()));
    }

    public Either<String, UUID> addMatchToLeague(NewMatchDTO newMatchDTO) {
        UUID leagueUUID = newMatchDTO.getLeagueUUID();
        return leagueManager.addMatchToLeague(leagueUUID, newMatchDTO);
    }

    public Either<LeagueError, List<MatchDTO>> getMatchesFromRound(UUID leagueUUID, int round) {
        return leagueManager.getMatchesFromRound(leagueUUID, round);
    }

    public Option<MatchDTO> getMatchByUUID(UUID uuid) {
        return matchManager.findByUUID(uuid).map(Match::toDTO);
    }

    public Option<LeagueDTO> getLeagueByUUID(UUID uuid) {
        return leagueManager.findLeagueByUUID(uuid).map(League::toDTO);
    }

    public Either<LeagueError, Match> setMatchResult(UUID matchUUID, MatchResult result) {
        return matchManager.setMatchResult(matchUUID, result);
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

    public Either<LeagueError, Boolean> hasMatchAlreadyBegun(UUID matchUUID) {
        return matchManager
                .findByUUID(matchUUID)
                .toEither(LeagueError.MATCH_NOT_FOUND)
                .map(match -> match.hasAlreadyBegun(timeProvider.now()));
    }

    public Either<LeagueError, League> archiveLeague(UUID leagueUUID) {
        return leagueManager.archiveLeague(leagueUUID);
    }
}
