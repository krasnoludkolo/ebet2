package pl.krasnoludkolo.ebet2.bet;

import io.vavr.control.Either;
import pl.krasnoludkolo.ebet2.bet.api.BetError;
import pl.krasnoludkolo.ebet2.bet.api.NewBet;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.MatchDTO;

import java.util.UUID;

final class NewBetValidator {

    private LeagueFacade leagueFacade;
    private Repository<Bet> repository;

    NewBetValidator(LeagueFacade leagueFacade, Repository<Bet> repository) {
        this.leagueFacade = leagueFacade;
        this.repository = repository;
    }

    Either<BetError, NewBet> validateParameters(NewBet newBet) {
        UUID matchUUID = newBet.getMatchUUID();
        String username = newBet.getUsername();

        if (matchNotExist(matchUUID)) {
            return Either.left(BetError.MATCH_NOT_FOUND);
        }
        if (matchHasAlreadyBegun(matchUUID)) {
            return Either.left(BetError.MATCH_ALREADY_STARTED);
        }
        if (betWithUsernameExist(matchUUID, username)) {
            return Either.left(BetError.BET_ALREADY_SET);
        }
        return Either.right(newBet);
    }

    private boolean matchNotExist(UUID uuid) {
        return leagueFacade.getMatchByUUID(uuid).map(MatchDTO::getUuid).isEmpty();
    }

    private boolean matchHasAlreadyBegun(UUID matchUUID) {
        return leagueFacade.hasMatchAlreadyBegun(matchUUID).getOrElse(false);
    }

    private boolean betWithUsernameExist(UUID matchUUID, String username) {
        return repository.findAll().find(bet -> bet.isCorrespondedToMatch(matchUUID) && bet.hasUsername(username)).isDefined();
    }

    Either<BetError, Bet> canUpdate(Bet bet) {
        return matchHasAlreadyBegun(bet.getMatchUuid())
                ?
                Either.left(BetError.MATCH_ALREADY_STARTED)
                :
                Either.right(bet);
    }


}
