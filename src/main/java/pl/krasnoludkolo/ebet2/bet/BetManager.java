package pl.krasnoludkolo.ebet2.bet;

import io.vavr.collection.List;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.bet.api.*;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;

import java.util.UUID;
import java.util.function.Predicate;

class BetManager {

    private Repository<Bet> repository;

    BetManager(Repository<Bet> repository) {
        this.repository = repository;
    }

    public UUID addBetToMatch(UUID matchUUID, NewBetDTO newBetDTO, String username) {
        if (betWithUsernameExist(matchUUID, username)) {
            throw new BetAlreadySet("Bet for username: " + username + " and match UUID " + matchUUID + " exists");
        }
        BetTyp betType = newBetDTO.getBetTyp();
        Bet bet = new Bet(matchUUID, username, betType);
        UUID uuid = bet.getUuid();
        repository.save(uuid, bet);
        return uuid;
    }

    private boolean betWithUsernameExist(UUID matchUUID, String username) {
        return repository.findAll().find(bet -> bet.isCorrespondedToMatch(matchUUID) && bet.hasUsername(username)).isDefined();
    }

    public Option<BetDTO> findBetByUUID(UUID betUUID) {
        return repository.findOne(betUUID).map(Bet::toDto);
    }

    public void updateBetToMatch(UUID betUUID, BetTyp betType) {
        Bet bet = repository.findOne(betUUID).getOrElseThrow(BetNotFound::new);
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
        return repository.findOne(betUUID).getOrElseThrow(BetNotFound::new).hasUsername(username);
    }
}
