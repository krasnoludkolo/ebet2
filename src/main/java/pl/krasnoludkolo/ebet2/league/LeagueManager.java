package pl.krasnoludkolo.ebet2.league;

import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.league.api.*;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

class LeagueManager {

    private Repository<League> leagueRepository;
    private MatchManager matchManager;

    LeagueManager(Repository<League> leagueRepository, MatchManager matchManager) {
        this.leagueRepository = leagueRepository;
        this.matchManager = matchManager;
    }

    League createLeague(String name) {
        if (invalidName(name)) {
            throw new LeagueNameException("Name is null or empty");
        } else if (containsLeagueWithName(name)) {
            throw new LeagueNameDuplicationException("Duplication name");
        }
        League league = League.createWithName(name);
        leagueRepository.save(league.getUuid(), league);
        return league;
    }

    private boolean invalidName(String name) {
        return name == null || name.isEmpty();
    }

    private boolean containsLeagueWithName(String name) {
        return leagueRepository
                .findAll()
                .find(withName(name))
                .isDefined();
    }

    Option<League> findLeagueByName(String name) {
        return leagueRepository
                .findAll()
                .find(withName(name));
    }

    private Predicate<League> withName(String name) {
        return league -> league.hasName(name);
    }

    Option<League> findLeagueByUUID(UUID uuid) {
        return leagueRepository.findOne(uuid);
    }

    List<LeagueDTO> getAllLeagues() {
        return leagueRepository.findAll().map(League::toDTO);
    }

    Either<String, UUID> addMatchToLeague(UUID leagueUUID, NewMatchDTO newMatchDTO) {
        return validateParameters(newMatchDTO)
                .map(newMatch -> addMatch(leagueUUID, newMatch));
    }

    private Either<String, NewMatchDTO> validateParameters(NewMatchDTO newMatchDTO) {
        if (Objects.isNull(newMatchDTO.getGuest()) || Objects.isNull(newMatchDTO.getHost())) {
            return Either.left("Team name cannot be null");
        }
        return Either.right(newMatchDTO);
    }

    private UUID addMatch(UUID leagueUUID, NewMatchDTO newMatch) {
        League league = findLeagueByUUID(leagueUUID).getOrElseThrow(LeagueNotFound::new);
        Match match = matchManager.createNewMatch(newMatch, league);
        league.addMatch(match);
        leagueRepository.update(leagueUUID, league);
        return match.getUuid();
    }

    List<MatchDTO> getMatchesFromRound(UUID leagueUUID, int round) {
        return leagueRepository
                .findOne(leagueUUID)
                .getOrElseThrow(LeagueNotFound::new)
                .getMatchesForRound(round);
    }

    public void removeMatchUUID(UUID leagueUUID) {
        leagueRepository.delete(leagueUUID);
    }

    public List<MatchDTO> getAllMatchesFromLeague(UUID uuid) {
        return leagueRepository.findOne(uuid).map(League::getAllMatches).getOrElse(List::empty);
    }
}
