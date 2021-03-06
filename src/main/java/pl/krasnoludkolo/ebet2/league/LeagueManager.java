package pl.krasnoludkolo.ebet2.league;

import io.vavr.API;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.league.api.LeagueError;
import pl.krasnoludkolo.ebet2.league.api.dto.LeagueDTO;
import pl.krasnoludkolo.ebet2.league.api.dto.MatchDTO;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

import static io.vavr.API.$;
import static io.vavr.API.Case;

class LeagueManager {

    private Repository<League> leagueRepository;
    private MatchManager matchManager;

    LeagueManager(Repository<League> leagueRepository, MatchManager matchManager) {
        this.leagueRepository = leagueRepository;
        this.matchManager = matchManager;
    }

    Either<LeagueError, League> createLeague(String name) {
        return validateName(name)
                .map(League::createWithName)
                .peek(league -> leagueRepository.save(league.getUuid(), league));
    }

    private Either<LeagueError, String> validateName(String name) {
        if (invalidName(name)) {
            return Either.left(LeagueError.WRONG_NAME_EXCEPTION);
        } else if (containsLeagueWithName(name)) {
            return Either.left(LeagueError.LEAGUE_NAME_DUPLICATION);
        }
        return Either.right(name);
    }

    private boolean invalidName(String name) {
        return name == null || name.isEmpty();
    }

    private boolean containsLeagueWithName(String name) {
        return leagueRepository
                .findAll()
                .exists(withName(name));
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

    Either<LeagueError, MatchDTO> addMatchToLeague(UUID leagueUUID, MatchDTO matchDTO) {
        return validateParameters(matchDTO)
                .flatMap(newMatch -> addMatch(leagueUUID, newMatch));
    }

    private Either<LeagueError, MatchDTO> validateParameters(MatchDTO matchDTO) {
        return API.Match(matchDTO)
                .option(
                        Case($(o -> Objects.isNull(o.getMatchStartDate())), LeagueError.MISSING_DATE),
                        Case($(o -> Objects.isNull(o.getHost())), LeagueError.EMPTY_OR_NULL_NAME),
                        Case($(o -> Objects.isNull(o.getGuest())), LeagueError.EMPTY_OR_NULL_NAME),
                        Case($(o -> o.getHost().isEmpty()), LeagueError.EMPTY_OR_NULL_NAME),
                        Case($(o -> o.getGuest().isEmpty()), LeagueError.EMPTY_OR_NULL_NAME)
                ).toEither(matchDTO)
                .swap();
    }

    private Either<LeagueError, MatchDTO> addMatch(UUID leagueUUID, MatchDTO newMatch) {
        return findLeagueByUUID(leagueUUID)
                .toEither(LeagueError.LEAGUE_NOT_FOUND)
                .map(league -> {
                    Match match = matchManager.createNewMatch(newMatch, league);
                    league.addMatch(match);
                    updateLeague(league);
                    return match.toDTO();
                });
    }

    private League updateLeague(League league) {
        leagueRepository.update(league.getUuid(), league);
        return league;
    }

    Either<LeagueError, List<MatchDTO>> getMatchesFromRound(UUID leagueUUID, int round) {
        return leagueRepository
                .findOne(leagueUUID)
                .map(league -> league.getMatchesForRound(round))
                .toEither(LeagueError.LEAGUE_NOT_FOUND);
    }

    void removeMatchUUID(UUID leagueUUID) {
        leagueRepository.delete(leagueUUID);
    }

    List<MatchDTO> getAllMatchesFromLeague(UUID uuid) {
        return leagueRepository.findOne(uuid).map(League::getAllMatches).getOrElse(List::empty);
    }

    Either<LeagueError, League> archiveLeague(UUID leagueUUID) {
        Either<LeagueError, League> league = leagueRepository.findOne(leagueUUID).toEither(LeagueError.LEAGUE_NOT_FOUND);
        return league
                .peek(l -> {
                    l.archive();
                    leagueRepository.save(leagueUUID, l);
                });

    }
}
