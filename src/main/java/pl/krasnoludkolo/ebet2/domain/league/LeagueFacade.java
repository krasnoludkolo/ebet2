package pl.krasnoludkolo.ebet2.domain.league;

import io.vavr.collection.List;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.domain.league.api.LeagueDTO;
import pl.krasnoludkolo.ebet2.domain.league.api.MatchDTO;
import pl.krasnoludkolo.ebet2.domain.league.api.MatchResult;
import pl.krasnoludkolo.ebet2.domain.league.api.NewMatchDTO;

import java.util.UUID;

public class LeagueFacade {


    private LeagueCRUDService leagueService;
    private MatchCRUDService matchService;

    public LeagueFacade(LeagueCRUDService leagueService, MatchCRUDService matchService) {
        this.leagueService = leagueService;
        this.matchService = matchService;
    }

    public UUID createLeague(String name) {
        League league = leagueService.createLeague(name);
        return league.getUuid();
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
    }
}
