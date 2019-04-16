package pl.krasnoludkolo.ebet2.bet;

import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.bet.api.BetError;
import pl.krasnoludkolo.ebet2.bet.api.BetTyp;
import pl.krasnoludkolo.ebet2.bet.api.NewBet;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;

import java.util.UUID;
import java.util.function.Predicate;

class BetManager {

    private Repository<Bet> repository;
    private NewBetValidator betValidator;

    BetManager(Repository<Bet> repository, LeagueFacade leagueFacade) {
        this.repository = repository;
        this.betValidator = new NewBetValidator(leagueFacade, repository);
    }

    Either<BetError, UUID> addBetToMatch(NewBet newBet) {
        return betValidator.validateParameters(newBet)
                .map(Bet::of)
                .peek(bet -> repository.save(bet.getUuid(), bet))
                .map(Bet::getUuid);
    }


    Option<BetDTO> findBetByUUID(UUID betUUID) {
        return repository.findOne(betUUID).map(Bet::toDto);
    }

    Either<BetError, UUID> updateBetToMatch(UUID betUUID, BetTyp betType) {
        return repository
                .findOne(betUUID)
                .toEither(BetError.BET_NOT_FOUND)
                .flatMap(betValidator::canUpdate)
                .peek(bet -> updateBet(betUUID, betType, bet))
                .map(Bet::getUuid);
    }


    private void updateBet(UUID betUUID, BetTyp betType, Bet bet) {
        bet.updateBetType(betType);
        repository.update(betUUID, bet);
    }

    void removeBet(UUID betUUID) {
        repository.delete(betUUID);
    }

    List<Bet> getAllBetsForMatch(UUID matchUUID) {
        return repository.findAll().filter(correspondMatch(matchUUID));
    }

    private Predicate<Bet> correspondMatch(UUID matchUUID) {
        return bet -> bet.isCorrespondedToMatch(matchUUID);
    }

    boolean correspondingUsername(UUID betUUID, UUID userUUID) {
        return repository.findOne(betUUID).map(bet -> bet.hasUserUUID(userUUID)).getOrElse(false);
    }
}
