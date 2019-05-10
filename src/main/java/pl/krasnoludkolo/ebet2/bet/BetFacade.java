package pl.krasnoludkolo.ebet2.bet;

import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.bet.api.*;
import pl.krasnoludkolo.ebet2.user.UserFacade;

import java.util.UUID;

public class BetFacade {

    private BetManager betManager;
    private UserFacade userFacade;

    public BetFacade(BetManager betManager, UserFacade userFacade) {
        this.betManager = betManager;
        this.userFacade = userFacade;
    }

    public Either<BetError, BetDTO> addBetToMatch(NewBetDTO newBetDTO, String auth) {
        return userFacade
                .getUserUUIDFromToken(auth)
                .mapLeft(x -> BetError.USER_NOT_FOUND)
                .map(userUUID -> new NewBet(newBetDTO.getBetTyp(), newBetDTO.getMatchUUID(), userUUID))
                .flatMap(betManager::addBetToMatch);
    }

    public Option<BetDTO> findBetByUUID(UUID betUUID) {
        return betManager.findBetByUUID(betUUID);
    }

    public Either<BetError, BetDTO> updateBetToMatch(UUID betUUID, BetTyp betType, String auth) {
        return userFacade.getUserUUIDFromToken(auth)
                .mapLeft(x -> BetError.USER_NOT_FOUND)
                .flatMap(uuid -> isCorrespondingUUID(uuid, betUUID))
                .flatMap(x -> betManager.updateBetToMatch(betUUID, betType));
    }

    private Either<BetError, UUID> isCorrespondingUUID(UUID uuid, UUID betUUID) {
        return betManager.correspondingUsername(betUUID, uuid) ? Either.right(uuid) : Either.left(BetError.BET_NOT_FOUND);
    }

    public void removeBet(UUID betUUID) {
        betManager.removeBet(betUUID);
    }

    public List<BetDTO> getAllBetsForMatch(UUID matchUUID) {
        return betManager.getAllBetsForMatch(matchUUID).map(Bet::toDto);
    }
}
