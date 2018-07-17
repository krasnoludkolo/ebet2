package pl.krasnoludkolo.ebet2.results;

import io.vavr.collection.List;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.bet.BetFacade;
import pl.krasnoludkolo.ebet2.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.MatchDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchNotFound;
import pl.krasnoludkolo.ebet2.results.api.LeagueResultsDTO;
import pl.krasnoludkolo.ebet2.results.api.UserResultDTO;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResultFacade {

    private final static Logger LOGGER = Logger.getLogger(ResultFacade.class.getName());


    private LeagueResultsManager crudService;
    private LeagueResultsUpdater leagueResultsUpdater;
    private BetFacade betFacade;
    private LeagueFacade leagueFacade;

    public ResultFacade(LeagueResultsManager service, LeagueResultsUpdater leagueResultsUpdater, BetFacade betFacade, LeagueFacade leagueFacade) {
        this.crudService = service;
        this.leagueResultsUpdater = leagueResultsUpdater;
        this.betFacade = betFacade;
        this.leagueFacade = leagueFacade;
    }

    public Option<LeagueResultsDTO> getResultsForLeague(UUID leagueUUID) {
        return Option.of(crudService.getResultsForLeague(leagueUUID).toDTO());
    }

    public Option<UserResultDTO> getResultsFromLeagueToUser(UUID leagueUUID, String user) {
        return Option.of(crudService
                .getResultsFromLeagueToUser(leagueUUID, user)
                .map(UserResult::toDTO)
                .reduce((a, b) -> new UserResultDTO(a.getName(), a.getPointCounter() + b.getPointCounter())));
    }

    public void updateLeagueResultsForMatch(UUID matchUUID) {
        MatchDTO matchDTO = leagueFacade.getMatchByUUID(matchUUID).getOrElseThrow(MatchNotFound::new);
        LOGGER.log(Level.INFO, "Updating results for match " + matchDTO.getHost() + " : " + matchDTO.getGuest() + " with uuid: " + matchDTO.getUuid());
        UUID leagueUUID = matchDTO.getLeagueUUID();
        LeagueResults resultsForLeague = crudService.getResultsForLeague(leagueUUID);
        List<BetDTO> bets = betFacade.getAllBetsForMatch(matchDTO.getUuid());
        LOGGER.log(Level.INFO, "Found " + bets.length() + " bets to update");
        leagueResultsUpdater.updateResultsForMatchInLeague(resultsForLeague, matchDTO, bets);
    }

}
