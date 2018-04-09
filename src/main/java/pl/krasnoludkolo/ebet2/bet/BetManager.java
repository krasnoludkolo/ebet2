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

    public UUID addBetToMatch(UUID matchUUID, NewBetDTO newBetDTO) {
        if (invalidUsername(newBetDTO.getUsername())) {
            throw new UsernameException("Username null or empty");
        } else if (matchHasBetWithUsername(matchUUID, newBetDTO.getUsername())) {
            throw new DuplicateUsername("Duplicate username");
        }
        String username = newBetDTO.getUsername();
        BetTyp betType = newBetDTO.getBetTyp();
        Bet bet = new Bet(matchUUID, username, betType);
        UUID uuid = bet.getUuid();
        repository.save(uuid, bet);
        return uuid;
    }

    private boolean invalidUsername(String username) {
        return username == null || username.isEmpty();
    }

    private boolean matchHasBetWithUsername(UUID matchUUID, String username) {
        return repository.findAll().filter(correspondMatch(matchUUID)).find(withUsername(username)).isDefined();
    }

    private Predicate<Bet> withUsername(String username) {
        return bet -> bet.hasUsername(username);
    }

    public Option<BetDTO> findBetByUUID(UUID betUUID) {
        return repository.findOne(betUUID).map(Bet::toDto);
    }

    public void updateBetToMatch(UUID betUUID, BetTyp betType) {
        Bet bet = repository.findOne(betUUID).getOrElseThrow(BetNotFound::new);
        bet.updateBetType(betType);
        repository.save(betUUID, bet);
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
}
