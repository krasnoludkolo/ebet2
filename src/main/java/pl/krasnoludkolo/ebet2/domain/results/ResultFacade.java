package pl.krasnoludkolo.ebet2.domain.results;

import io.vavr.collection.List;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.domain.bet.BetFacade;
import pl.krasnoludkolo.ebet2.domain.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.domain.league.api.MatchDTO;
import pl.krasnoludkolo.ebet2.domain.league.api.MatchResult;
import pl.krasnoludkolo.ebet2.domain.results.api.LeagueResultsDTO;
import pl.krasnoludkolo.ebet2.domain.results.api.UserResultDTO;

import java.util.UUID;

public class ResultFacade {

    private LeagueResultsCRUDService crudService;
    private LeagueResultsUpdater leagueResultsUpdater;
    private BetFacade betFacade;

    public ResultFacade(LeagueResultsCRUDService service, LeagueResultsUpdater leagueResultsUpdater, BetFacade betFacade) {
        this.crudService = service;
        this.leagueResultsUpdater = leagueResultsUpdater;
        this.betFacade = betFacade;
    }

    public Option<LeagueResultsDTO> getResultsForLeague(UUID leagueUUID) {
        return Option.of(crudService.getResultsForLeague(leagueUUID).toDTO());
    }

    public void createResultsForLeague(UUID uuid) {
        crudService.createResultsForLeague(uuid);
    }

    public Option<UserResultDTO> getResultsFromLeagueToUser(UUID leagueUUID, String user) {
        return crudService.getResultsFromLeagueToUser(leagueUUID, user).map(UserResult::toDTO);
    }

    public void updateLeagueResultsForMatch(MatchDTO matchDTO) {
        UUID leagueUUID = matchDTO.getLeagueUUID();
        LeagueResults resultsForLeague = crudService.getResultsForLeague(leagueUUID);
        MatchResult matchResult = matchDTO.getResult();
        List<BetDTO> bets = betFacade.getAllBetsForMatch(matchDTO.getUuid());
        leagueResultsUpdater.updateResultsForMatchInLeague(resultsForLeague, matchResult, bets);
    }

}
