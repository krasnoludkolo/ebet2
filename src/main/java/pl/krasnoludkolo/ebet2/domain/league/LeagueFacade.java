package pl.krasnoludkolo.ebet2.domain.league;

import io.vavr.collection.List;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.domain.league.api.LeagueDTO;
import pl.krasnoludkolo.ebet2.domain.league.api.MatchDTO;
import pl.krasnoludkolo.ebet2.domain.league.api.MatchResult;
import pl.krasnoludkolo.ebet2.domain.league.api.NewMatchDTO;
import pl.krasnoludkolo.ebet2.domain.results.ResultFacade;

import java.util.UUID;

public class LeagueFacade {


    private LeagueCRUDService leagueService;
    private MatchCRUDService matchService;
    private ResultFacade resultFacade;

    public LeagueFacade(LeagueCRUDService leagueService, MatchCRUDService matchService, ResultFacade resultFacade) {
        this.leagueService = leagueService;
        this.matchService = matchService;
        this.resultFacade = resultFacade;
    }

    public UUID createLeague(String name) {
        League league = leagueService.createLeague(name);
        UUID uuid = league.getUuid();
        resultFacade.createResultsForLeague(uuid);
        return uuid;
    }

    public Option<LeagueDTO> findLeagueByName(String name) {
        return leagueService.findLeagueByName(name).map(League::toDTO);
    }

    public List<LeagueDTO> getAllLeagues() {
        return leagueService.getAllLeagues();
    }

    public UUID addMatchToLeague(UUID leagueUUID, NewMatchDTO newMatchDTO) {
        Match match = matchService.createNewMatch(newMatchDTO);
        leagueService.addMatchToLeague(leagueUUID, match);
        return match.getUuid();
    }

    public List<MatchDTO> getMatchesFromRound(UUID leagueUUID, int round) {
        return leagueService.getMatchesFromRound(leagueUUID, round);
    }

    public Option<MatchDTO> getMatchByUUID(UUID uuid) {
        return matchService.findByUUID(uuid).map(Match::toDTO);
    }

    public Option<LeagueDTO> getLeagueByUUID(UUID uuid) {
        return leagueService.findLeagueByUUID(uuid).map(League::toDTO);
    }

    public void setMatchResult(UUID matchUUID, MatchResult result) {
        matchService.setMatchResult(matchUUID, result);
        MatchDTO matchDTO = matchService.findByUUID(matchUUID).getOrElseThrow(IllegalStateException::new).toDTO();
        resultFacade.updateLeagueResultsForMatch(matchDTO);
    }

    public void removeMatch(UUID matchUUID) {
        matchService.removeMatchByUUID(matchUUID);
    }

    public void removeLeague(UUID leagueUUID) {
        leagueService.removeMatchUUID(leagueUUID);
    }
}
