package pl.krasnoludkolo.ebet2.domain.league;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.krasnoludkolo.ebet2.domain.league.api.LeagueDTO;
import pl.krasnoludkolo.ebet2.domain.league.api.NewMatchDTO;

import java.util.List;
import java.util.UUID;

@RestController
class LeagueController {

    private LeagueFacade leagueFacade;

    @Autowired
    LeagueController(LeagueFacade leagueFacade) {
        this.leagueFacade = leagueFacade;
    }

    @PostMapping("/league")
    public HttpEntity<String> createNewLeague(String name) {
        UUID leagueUUID = leagueFacade.createLeague(name);
        return new ResponseEntity<>(leagueUUID.toString(), HttpStatus.CREATED);
    }

    @GetMapping("/leagues")
    public HttpEntity<List<LeagueDTO>> getAllLeagues() {
        List<LeagueDTO> allLeagues = leagueFacade.getAllLeagues().asJava();
        return new ResponseEntity<>(allLeagues, HttpStatus.OK);
    }

    @PostMapping("/match")
    public HttpEntity<UUID> addMatchToLeague(@RequestBody NewMatchRequestWrapper newMatchRequestWrapper) {
        UUID uuid = leagueFacade.addMatchToLeague(UUID.fromString(newMatchRequestWrapper.uuid), newMatchRequestWrapper.matchDTO);
        return new ResponseEntity<>(uuid, HttpStatus.CREATED);
    }

//    @GetMapping("/matchs")
//    public HttpEntity<UUID> getAllMatchesFromRound(int round){
//        leagueFacade.getMatchesFromRound();
//    }

    static class NewMatchRequestWrapper {
        String uuid;
        NewMatchDTO matchDTO;

        public NewMatchRequestWrapper() {
        }

        public NewMatchRequestWrapper(String uuid, NewMatchDTO matchDTO) {
            this.uuid = uuid;
            this.matchDTO = matchDTO;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public NewMatchDTO getMatchDTO() {
            return matchDTO;
        }

        public void setMatchDTO(NewMatchDTO matchDTO) {
            this.matchDTO = matchDTO;
        }
    }
}
