package pl.krasnoludkolo.ebet2.points;

import io.vavr.collection.List;
import pl.krasnoludkolo.ebet2.bet.BetFacade;
import pl.krasnoludkolo.ebet2.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.infrastructure.Success;
import pl.krasnoludkolo.ebet2.league.api.MatchDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;

class LeagueResultsUpdater {

    private LeagueResultsManager leagueResultsManager;
    private BetFacade betFacade;

    LeagueResultsUpdater(LeagueResultsManager leagueResultsManager, BetFacade betFacade) {
        this.leagueResultsManager = leagueResultsManager;
        this.betFacade = betFacade;
    }

    Success updateResultsForMatchInLeague(MatchDTO matchDTO, MatchResult result) {
        LeagueResults resultsForLeague = leagueResultsManager.getResultsForLeague(matchDTO.getLeagueUUID());
        int round = matchDTO.getRound();
        List<BetDTO> bets = betFacade.getAllBetsForMatch(matchDTO.getUuid());
        LeagueResults updatedResults = resultsForLeague.updateResults(result, bets, round);
        leagueResultsManager.updateLeagueResult(updatedResults);
        return new Success();
    }

}
