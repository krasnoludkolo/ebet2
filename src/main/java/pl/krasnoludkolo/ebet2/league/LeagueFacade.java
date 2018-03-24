package pl.krasnoludkolo.ebet2.league;

import io.vavr.collection.List;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.league.api.LeagueDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;
import pl.krasnoludkolo.ebet2.league.api.NewMatchDTO;
import pl.krasnoludkolo.ebet2.results.ResultFacade;

import javax.transaction.Transactional;
import java.util.UUID;

@Transactional
public class LeagueFacade {

    private LeagueManager leagueManager;
    private MatchManager matchManager;
    private ResultFacade resultFacade;

    public LeagueFacade(LeagueManager leagueManager, MatchManager matchManager, ResultFacade resultFacade) {
        this.leagueManager = leagueManager;
        this.matchManager = matchManager;
        this.resultFacade = resultFacade;
    }

    public UUID createLeague(String name) {
        League league = leagueManager.createLeague(name);
        UUID uuid = league.getUuid();
        resultFacade.createResultsForLeague(uuid);
        return uuid;
    }

    public Option<LeagueDTO> findLeagueByName(String name) {
        return leagueManager.findLeagueByName(name).map(League::toDTO);
    }

    public List<LeagueDTO> getAllLeagues() {
        return leagueManager.getAllLeagues();
    }

    public UUID addMatchToLeague(NewMatchDTO newMatchDTO) {
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
        MatchDTO matchDTO = matchManager.findByUUID(matchUUID).getOrElseThrow(IllegalStateException::new).toDTO();
        resultFacade.updateLeagueResultsForMatch(matchDTO);
    }

    public void removeMatch(UUID matchUUID) {
        matchManager.removeMatchByUUID(matchUUID);
    }

    public void removeLeague(UUID leagueUUID) {
        leagueManager.removeMatchUUID(leagueUUID);
    }
}
