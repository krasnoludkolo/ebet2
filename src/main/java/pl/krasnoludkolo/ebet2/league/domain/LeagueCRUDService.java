package pl.krasnoludkolo.ebet2.league.domain;

import io.vavr.collection.List;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.league.api.LeagueDTO;
import pl.krasnoludkolo.ebet2.league.api.LeagueNameDuplicationException;
import pl.krasnoludkolo.ebet2.league.api.LeagueNotFound;
import pl.krasnoludkolo.ebet2.league.api.MatchDTO;

import java.util.UUID;
import java.util.function.Predicate;

class LeagueCRUDService {

    private Repository<League> leagueRepository;

    LeagueCRUDService(Repository<League> leagueRepository) {
        this.leagueRepository = leagueRepository;
    }

    League createLeague(String name) {
        if (containsLeagueWithName(name)) {
            throw new LeagueNameDuplicationException();
        }
        League league = new League(name);
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

    void addMatchToLeague(UUID leagueUUID, Match match) {
        League league = findLeagueByUUID(leagueUUID).getOrElseThrow(LeagueNotFound::new);
        league.addMatch(match);
        leagueRepository.update(leagueUUID, league);

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
