package pl.krasnoludkolo.ebet2.league;

import io.haste.TimeSource;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.league.api.LeagueError;
import pl.krasnoludkolo.ebet2.league.api.dto.*;

import java.util.UUID;

public class LeagueFacade {

    private LeagueManager leagueManager;
    private MatchManager matchManager;
    private TimeSource timeSource;

    public LeagueFacade(LeagueManager leagueManager, MatchManager matchManager, TimeSource timeSource) {
        this.leagueManager = leagueManager;
        this.matchManager = matchManager;
        this.timeSource = timeSource;
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

    public Either<LeagueError, MatchDTO> addMatchToLeague(NewMatchDTO newMatchDTO) {
        UUID leagueUUID = newMatchDTO.getLeagueUUID();
        MatchDTO matchDTO = MatchDTO.fromNewMatchDTO(newMatchDTO);
        return leagueManager.addMatchToLeague(leagueUUID, matchDTO);
    }

    public Either<LeagueError, MatchDTO> addFinishedMatchToLeague(MatchDTO matchDTO) {
        UUID leagueUUID = matchDTO.getLeagueUUID();
        return leagueManager.addMatchToLeague(leagueUUID, matchDTO);
    }

    public Either<LeagueError, List<MatchDTO>> getMatchesFromRound(UUID leagueUUID, int round) {
        return leagueManager.getMatchesFromRound(leagueUUID, round);
    }

    public Either<LeagueError, MatchDTO> getMatchByUUID(UUID uuid) {
        return matchManager.findByUUID(uuid).map(Match::toDTO).toEither(LeagueError.MATCH_NOT_FOUND);
    }

    public Option<LeagueDTO> getLeagueByUUID(UUID uuid) {
        return leagueManager.findLeagueByUUID(uuid).map(League::toDTO);
    }

    public Either<LeagueError, MatchDTO> setMatchResult(UUID matchUUID, MatchResult result) {
        return matchManager.setMatchResult(matchUUID, result).map(Match::toDTO);
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
                .map(match -> match.hasAlreadyBegun(timeSource.now()));
    }

    public Either<LeagueError, League> archiveLeague(UUID leagueUUID) {
        return leagueManager.archiveLeague(leagueUUID);
    }
}
