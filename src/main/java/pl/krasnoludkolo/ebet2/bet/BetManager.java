package pl.krasnoludkolo.ebet2.bet;

import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.bet.api.BetTyp;
import pl.krasnoludkolo.ebet2.bet.api.NewBetDTO;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.MatchDTO;

import java.util.UUID;
import java.util.function.Predicate;

class BetManager {

    private Repository<Bet> repository;
    private LeagueFacade leagueFacade;

    BetManager(Repository<Bet> repository, LeagueFacade leagueFacade) {
        this.repository = repository;
        this.leagueFacade = leagueFacade;
    }

    public Either<String, UUID> addBetToMatch(UUID matchUUID, NewBetDTO newBetDTO, String username) {
        return validateParameters(matchUUID, username)
                .flatMap(this::matchExist)
                .map(match -> {
                    BetTyp betType = newBetDTO.getBetTyp();
                    Bet bet = new Bet(match, username, betType);
                    UUID uuid = bet.getUuid();
                    repository.save(uuid, bet);
                    return uuid;
                });
    }

    private Either<String, UUID> matchExist(UUID uuid) {
        return leagueFacade.getMatchByUUID(uuid).map(MatchDTO::getUuid).toEither("Match not found");
    }

    private Either<String, UUID> validateParameters(UUID matchUUID, String username) {

        if (matchHasAlreadyBegun(matchUUID)) {
            return Either.left("Match has already begun");
        }
        if (betWithUsernameExist(matchUUID, username)) {
            return Either.left("Bet for username: " + username + " and match UUID " + matchUUID + " exists");
        }
        return Either.right(matchUUID);
    }

    private boolean matchHasAlreadyBegun(UUID matchUUID) {
        return leagueFacade.hasMatchAlreadyBegun(matchUUID).getOrElse(false);
    }

    private boolean betWithUsernameExist(UUID matchUUID, String username) {
        return repository.findAll().find(bet -> bet.isCorrespondedToMatch(matchUUID) && bet.hasUsername(username)).isDefined();
    }

    public Option<BetDTO> findBetByUUID(UUID betUUID) {
        return repository.findOne(betUUID).map(Bet::toDto);
    }

    public Either<String, UUID> updateBetToMatch(UUID betUUID, BetTyp betType) {
        return repository
                .findOne(betUUID)
                .toEither("Bet not found")
                .peek(bet -> {
                    if (!matchHasAlreadyBegun(bet.getMatchUuid())) {
                        updateBet(betUUID, betType, bet);
                    }
                })
                .map(Bet::getUuid);
    }

    private void updateBet(UUID betUUID, BetTyp betType, Bet bet) {
        bet.updateBetType(betType);
        repository.update(betUUID, bet);
    }

    public void removeBet(UUID betUUID) {
        repository.delete(betUUID);
    }

    public List<Bet> getAllBetsForMatch(UUID matchUUID) {
        return repository.findAll().filter(correspondMatch(matchUUID));
    }

    private Predicate<Bet> correspondMatch(UUID matchUUID) {
        return bet -> bet.isCorrespondedToMatch(matchUUID);
    }

    public boolean correspondingUsername(UUID betUUID, String username) {
        return repository.findOne(betUUID).map(bet -> bet.hasUsername(username)).getOrElse(false);
    }
}
