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
        UUID userUUID = newBet.getUserUUID();

        if (matchNotExist(matchUUID)) {
            return Either.left(BetError.MATCH_NOT_FOUND);
        }
        if (matchHasAlreadyBegun(matchUUID)) {
            return Either.left(BetError.MATCH_ALREADY_STARTED);
        }
        if (betWithUUIDExist(matchUUID, userUUID)) {
            return Either.left(BetError.BET_ALREADY_SET);
        }
        return Either.right(newBet);
    }

    private boolean matchNotExist(UUID uuid) {
        return leagueFacade.getMatchByUUID(uuid).map(MatchDTO::getUuid).isEmpty();
    }

    private boolean betWithUUIDExist(UUID matchUUID, UUID userUUID) {
        return repository.findAll().find(bet -> bet.isCorrespondedToMatch(matchUUID) && bet.hasUserUUID(userUUID)).isDefined();
    }

    Either<BetError, Bet> canUpdate(Bet bet) {
        return matchHasAlreadyBegun(bet.getMatchUuid())
                ?
                Either.left(BetError.MATCH_ALREADY_STARTED)
                :
                Either.right(bet);
    }

    private boolean matchHasAlreadyBegun(UUID matchUUID) {
        return leagueFacade.hasMatchAlreadyBegun(matchUUID).getOrElse(false);
    }


}
