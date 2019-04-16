package pl.krasnoludkolo.ebet2.bet;

import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.bet.api.*;
import pl.krasnoludkolo.ebet2.user.UserFacade;
import pl.krasnoludkolo.ebet2.user.api.UserError;

import java.util.UUID;

public class BetFacade {

    private BetManager betManager;
    private UserFacade userFacade;

    public BetFacade(BetManager betManager, UserFacade userFacade) {
        this.betManager = betManager;
        this.userFacade = userFacade;
    }

    public Either<BetError, UUID> addBetToMatch(NewBetDTO newBetDTO, String auth) {
        return userFacade
                .getUsername(auth)
                .mapLeft(x -> BetError.USER_NOT_FOUND)
                .map(user -> new NewBet(newBetDTO.getBetTyp(), newBetDTO.getMatchUUID(), user))
                .flatMap(betManager::addBetToMatch);
    }

    public Option<BetDTO> findBetByUUID(UUID betUUID) {
        return betManager.findBetByUUID(betUUID);
    }

    //TODO refactor
    public Either<BetError, UUID> updateBetToMatch(UUID betUUID, BetTyp betType, String auth) {
        Either<UserError, String> username = userFacade.getUsername(auth);
        if (username.isLeft()) {
            return Either.left(BetError.USER_NOT_FOUND);
        }
        if (!betManager.correspondingUsername(betUUID, username.get())) {
            return Either.left(BetError.BET_NOT_FOUND);
        }
        return betManager.updateBetToMatch(betUUID, betType);
    }

    public void removeBet(UUID betUUID) {
        betManager.removeBet(betUUID);
    }

    public List<BetDTO> getAllBetsForMatch(UUID matchUUID) {
        return betManager.getAllBetsForMatch(matchUUID).map(Bet::toDto);
    }
}
