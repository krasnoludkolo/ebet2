package pl.krasnoludkolo.ebet2.league;

import io.vavr.collection.List;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.league.api.*;

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
        if (containsLeagueWithName(name)) {
            throw new LeagueNameDuplicationException();
        }
        League league = League.createWithName(name);
        leagueRepository.save(league.getUuid(), league);
        return league;
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

    UUID addMatchToLeague(UUID leagueUUID, NewMatchDTO newMatchDTO) {
        League league = findLeagueByUUID(leagueUUID).getOrElseThrow(LeagueNotFound::new);
        Match match = matchManager.createNewMatch(newMatchDTO, league);
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
}
