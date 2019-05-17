package pl.krasnoludkolo.ebet2.bet;

import io.vavr.control.Either;
import pl.krasnoludkolo.ebet2.bet.api.BetError;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.MatchDTO;

import java.util.UUID;
import java.util.function.Predicate;

import static io.vavr.API.*;

final class NewBetValidator {

    private LeagueFacade leagueFacade;
    private Repository<Bet> repository;

    NewBetValidator(LeagueFacade leagueFacade, Repository<Bet> repository) {
        this.leagueFacade = leagueFacade;
        this.repository = repository;
    }

    Either<BetError, NewBet> validateParameters(NewBet newBet) {
        return Match(newBet).of(
                Case($(matchNotExist()), Either.left(BetError.MATCH_NOT_FOUND)),
                Case($(betWithUUIDExist()), Either.left(BetError.BET_ALREADY_SET)),
                Case($(matchHasAlreadyBegun()), Either.left(BetError.MATCH_ALREADY_STARTED)),
                Case($(), Either.right(newBet))
        );
    }

    private Predicate<NewBet> matchNotExist() {
        return bet -> leagueFacade.getMatchByUUID(bet.getMatchUUID()).map(MatchDTO::getUuid).isEmpty();
    }

    private Predicate<NewBet> betWithUUIDExist() {
        return b -> repository
                .findAll()
                .find(bet -> bet.isCorrespondedToMatch(b.getMatchUUID()) && bet.hasUserUUID(b.getUserUUID())).isDefined();
    }

    private Predicate<NewBet> matchHasAlreadyBegun() {
        return bet -> matchHasAlreadyBegun(bet.getMatchUUID());
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
